package org.tsdl.implementation.evaluation;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.tsdl.implementation.factory.TsdlComponentFactory;
import org.tsdl.implementation.model.choice.AnnotatedTsdlPeriod;
import org.tsdl.infrastructure.api.QueryService;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.infrastructure.model.MultipleScalarResult;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.infrastructure.model.SingularScalarResult;
import org.tsdl.infrastructure.model.TsdlDataPoints;
import org.tsdl.infrastructure.model.TsdlLogEvent;
import org.tsdl.infrastructure.model.TsdlPeriod;
import org.tsdl.infrastructure.model.TsdlPeriodSet;

/**
 * Default implementation of {@link QueryService}.
 */
@Slf4j
public class TsdlQueryService implements QueryService {
  @Override
  public QueryResult query(List<DataPoint> data, String query) {
    final var parser = TsdlComponentFactory.INSTANCE.queryParser();
    final var resultCollector = TsdlComponentFactory.INSTANCE.resultCollector();
    final var periodAssembler = TsdlComponentFactory.INSTANCE.periodAssembler();
    final var samplesCalculator = TsdlComponentFactory.INSTANCE.samplesCalculator();

    try {
      Conditions.checkNotNull(Condition.ARGUMENT, data, "Data must not be null.");
      Conditions.checkNotNull(Condition.ARGUMENT, query, "Query string must not be null.");
      log.info("Evaluating query '{}'", query);

      var parsedQuery = parser.parseQuery(query);
      var logEvents = new ArrayList<TsdlLogEvent>();

      samplesCalculator.setSummaryStatisticsCalculator(parsedQuery.samples());
      var sampleValues = samplesCalculator.computeSampleValues(parsedQuery.samples(), data, logEvents);
      samplesCalculator.setConnectiveArgumentValues(parsedQuery, sampleValues);

      log.info("Applying query filters to {} initial data points.", data.size());
      var relevantDataPoints = parsedQuery.filter().isPresent()
          ? parsedQuery.filter().get().evaluateFilters(data)
          : data;
      log.info("After filter application, {} relevant data points are remaining.", relevantDataPoints.size());

      log.info("Detecting periods based on the query's event definitions.");
      var detectedPeriods = periodAssembler.assemble(relevantDataPoints, parsedQuery.events());
      log.info("Detected {} periods based on the query's event definitions.", detectedPeriods.size());

      TsdlPeriodSet periodSet;
      boolean noPeriodDefinitions;
      if (parsedQuery.choice().isPresent()) {
        periodSet = parsedQuery.choice().get().evaluate(detectedPeriods);
        noPeriodDefinitions = false;
      } else if (!detectedPeriods.isEmpty()) {
        periodSet = QueryResult.of(detectedPeriods.size(), detectedPeriods.stream().map(AnnotatedTsdlPeriod::period).toList());
        noPeriodDefinitions = false;
      } else {
        periodSet = TsdlPeriodSet.EMPTY;
        noPeriodDefinitions = true;
      }

      var result = resultCollector.collect(
          parsedQuery.result(),
          relevantDataPoints,
          periodSet,
          noPeriodDefinitions,
          sampleValues
      );

      var finalResult = result.withLogs(logEvents);
      log.info("Evaluated query to {}", getResultLogRepresentation(finalResult));
      return finalResult;
    } catch (TsdlEvaluationException e) {
      throw e;
    } catch (Exception e) {
      throw new TsdlEvaluationException("Query evaluation failed.", e);
    }
  }

  private String getResultLogRepresentation(QueryResult result) {
    var description = switch (result) {
      case TsdlDataPoints dps -> "%s data points".formatted(dps.items().size());
      case MultipleScalarResult scalars -> "%s sample values".formatted(scalars.values().size());
      case SingularScalarResult scalar -> "value %s".formatted(scalar.value());
      case TsdlPeriod period ->
          "%s period from %s until %s with index %s".formatted(period.isEmpty() ? "empty" : "non-empty", period.start(), period.end(),
              period.index());
      case TsdlPeriodSet periods -> "%s periods".formatted(periods.totalPeriods());
      default -> throw Conditions.exception(Condition.STATE, "Unknown query result type '%s", result.getClass().getName());
    };

    return "%s with %s and %s log events.".formatted(result.type(), description, result.logs().size());
  }
}
