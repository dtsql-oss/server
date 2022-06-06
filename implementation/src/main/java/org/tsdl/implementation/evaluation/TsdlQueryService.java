package org.tsdl.implementation.evaluation;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.tsdl.implementation.factory.ObjectFactory;
import org.tsdl.implementation.model.TsdlQuery;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.filter.NegatedSinglePointFilter;
import org.tsdl.implementation.model.filter.SinglePointFilter;
import org.tsdl.implementation.model.filter.ThresholdFilter;
import org.tsdl.implementation.model.filter.argument.TsdlSampleFilterArgument;
import org.tsdl.implementation.model.sample.TsdlSample;
import org.tsdl.implementation.parsing.TsdlQueryParser;
import org.tsdl.infrastructure.api.QueryService;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.infrastructure.model.TsdlPeriod;

/**
 * Default implementation of {@link QueryService}.
 */
@Slf4j
public class TsdlQueryService implements QueryService {
  private final TsdlQueryParser parser = ObjectFactory.INSTANCE.queryParser();

  @Override
  public QueryResult query(List<DataPoint> data, String query) {
    try {
      Conditions.checkNotNull(Condition.ARGUMENT, data, "Data must not be null.");
      Conditions.checkNotNull(Condition.ARGUMENT, query, "Query string must not be null.");
      log.info("Evaluating query '{}'", query);

      var parsedQuery = parser.parseQuery(query);

      final var sampleValues = computeSamples(parsedQuery.samples(), data);
      setSampleFilterArgumentValues(parsedQuery, sampleValues);

      var filteredDataPoints = parsedQuery.filter().isPresent() ? parsedQuery.filter().get().evaluateFilters(data) : data;

      var eventMarkers = new HashMap<TsdlIdentifier, Instant>();
      var periods = new HashMap<TsdlIdentifier, List<TsdlPeriod>>();
      if (!parsedQuery.events().isEmpty()) {
        for (var i = 0; i < filteredDataPoints.size(); i++) {
          var dp = filteredDataPoints.get(i);
          for (var event : parsedQuery.events()) {
            var eventId = event.identifier();
            if (event.definition().isSatisfied(dp)) {
              // satisfied - either period is still going on or the period starts
              if (eventMarkers.containsKey(eventId)) {
                // period is still going on

                if (i == filteredDataPoints.size() - 1) {
                  // if, however, the end of the data is reached, the period must end, too
                  periods.putIfAbsent(eventId, new ArrayList<>());

                  var periodStart = eventMarkers.get(eventId);
                  var periodEnd = dp.getTimestamp();
                  var index = periods.get(eventId).size();

                  var period = QueryResult.of(index, periodStart, periodEnd);
                  periods.get(eventId).add(period);

                  eventMarkers.remove(eventId);
                }
              } else {
                // new period starts
                eventMarkers.put(eventId, dp.getTimestamp());
              }
            } else {
              // not satisfied - either period ends, or there is still no open period
              if (eventMarkers.containsKey(eventId)) {
                // period ended
                periods.putIfAbsent(eventId, new ArrayList<>());

                var periodStart = eventMarkers.get(eventId);
                var periodEnd = filteredDataPoints.get(i - 1).getTimestamp();
                var index = periods.get(eventId).size();

                var period = QueryResult.of(index, periodStart, periodEnd);
                periods.get(eventId).add(period);

                eventMarkers.remove(eventId);
              } else {
                // nothing to do - still no open period
              }
            }
          }
        }
      }

      // TODO fix indices in periods, in conjunction wtih CHOOSE - implement CHOOSE

      return QueryResult.of(filteredDataPoints);
    } catch (TsdlEvaluationException e) {
      throw e;
    } catch (Exception e) {
      throw new TsdlEvaluationException("Query evaluation failed.", e);
    }
  }

  private Map<TsdlIdentifier, Double> computeSamples(List<TsdlSample> samples, List<DataPoint> input) {
    return samples.stream().collect(Collectors.toMap(
            TsdlSample::identifier,
            sample -> sample.aggregator().compute(input)
        )
    );
  }

  private void setSampleFilterArgumentValues(TsdlQuery query, Map<TsdlIdentifier, Double> sampleValues) {
    if (query.filter().isPresent()) {
      setThresholdFilterSampleArguments(
          createThresholdFilterStream(query.filter().get().filters()),
          sampleValues
      );
    }

    var allEventFilters = query.events().stream().flatMap(event -> event.definition().filters().stream()).toList();
    setThresholdFilterSampleArguments(
        createThresholdFilterStream(allEventFilters),
        sampleValues
    );
  }

  private Stream<ThresholdFilter> createThresholdFilterStream(List<SinglePointFilter> filters) {
    // constructs like lt(sampleIdentifier)
    var nonNegated = filters.stream()
        .filter(ThresholdFilter.class::isInstance)
        .map(ThresholdFilter.class::cast)
        .filter(filter -> filter.threshold() instanceof TsdlSampleFilterArgument);

    // constructs like NOT(gt(sampleIdentifier))
    var negated = filters.stream()
        .filter(NegatedSinglePointFilter.class::isInstance)
        .map(NegatedSinglePointFilter.class::cast)
        .filter(filter -> filter.filter() instanceof ThresholdFilter)
        .map(filter -> (ThresholdFilter) filter.filter())
        .filter(filter -> filter.threshold() instanceof TsdlSampleFilterArgument);

    return Stream.concat(nonNegated, negated);
  }

  @SuppressWarnings("ConstantConditions") // method too complex for this inspection due to the "filterArgument" pattern variable, but logic is fine
  private void setThresholdFilterSampleArguments(Stream<ThresholdFilter> filters, Map<TsdlIdentifier, Double> sampleValues) {
    filters.forEach(thresholdFilter -> {
      if (!(thresholdFilter.threshold() instanceof TsdlSampleFilterArgument filterArgument)) {
        throw Conditions.exception(Condition.ARGUMENT, "Filter argument must reference a sample (non-literal).");
      }

      var argumentIdentifier = filterArgument.sample().identifier();
      if (!sampleValues.containsKey(argumentIdentifier)) {
        throw new TsdlEvaluationException(
            "Sample '%s' referenced by filter has not been computed. Is it declared in the 'SAMPLES' directive?".formatted(
                argumentIdentifier.name())
        );
      }

      filterArgument.setValue(sampleValues.get(argumentIdentifier));
    });
  }
}
