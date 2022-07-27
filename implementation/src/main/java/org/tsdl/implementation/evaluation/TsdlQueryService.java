package org.tsdl.implementation.evaluation;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.tsdl.implementation.evaluation.impl.TsdlPeriodAssembler;
import org.tsdl.implementation.factory.TsdlComponentFactory;
import org.tsdl.implementation.parsing.TsdlQueryParser;
import org.tsdl.infrastructure.api.QueryService;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.infrastructure.model.TsdlLogEvent;

/**
 * Default implementation of {@link QueryService}.
 */
@Slf4j
public class TsdlQueryService implements QueryService {
  private final TsdlQueryParser parser = TsdlComponentFactory.INSTANCE.queryParser();
  private final TsdlResultCollector resultCollector = TsdlComponentFactory.INSTANCE.resultCollector();
  private final TsdlPeriodAssembler periodAssembler = TsdlComponentFactory.INSTANCE.periodAssembler();
  private final TsdlSamplesCalculator samplesCalculator = TsdlComponentFactory.INSTANCE.samplesCalculator();

  @Override
  public QueryResult query(List<DataPoint> data, String query) {
    try {
      Conditions.checkNotNull(Condition.ARGUMENT, data, "Data must not be null.");
      Conditions.checkNotNull(Condition.ARGUMENT, query, "Query string must not be null.");
      log.info("Evaluating query '{}'", query);

      var parsedQuery = parser.parseQuery(query);
      var logEvents = new ArrayList<TsdlLogEvent>();

      var sampleValues = samplesCalculator.computeSampleValues(parsedQuery.samples(), data, logEvents);
      samplesCalculator.setConnectiveArgumentValues(parsedQuery, sampleValues);

      var relevantDataPoints = parsedQuery.filter().isPresent()
          ? parsedQuery.filter().get().evaluateFilters(data)
          : data;

      var detectedPeriods = periodAssembler.assemble(relevantDataPoints, parsedQuery.events());

      var result = resultCollector.collect(
          parsedQuery.result(),
          relevantDataPoints,
          detectedPeriods,
          parsedQuery.choice().orElse(null),
          sampleValues
      );

      return result.withLogs(logEvents);
    } catch (TsdlEvaluationException e) {
      throw e;
    } catch (Exception e) {
      throw new TsdlEvaluationException("Query evaluation failed.", e);
    }
  }
}
