package org.tsdl.implementation.evaluation;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.tsdl.implementation.evaluation.impl.choice.AnnotatedTsdlPeriodImpl;
import org.tsdl.implementation.factory.ObjectFactory;
import org.tsdl.implementation.model.TsdlQuery;
import org.tsdl.implementation.model.choice.AnnotatedTsdlPeriod;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.event.TsdlEvent;
import org.tsdl.implementation.model.filter.NegatedSinglePointFilter;
import org.tsdl.implementation.model.filter.SinglePointFilter;
import org.tsdl.implementation.model.filter.threshold.ThresholdFilter;
import org.tsdl.implementation.model.filter.threshold.argument.TsdlSampleFilterArgument;
import org.tsdl.implementation.model.result.YieldFormat;
import org.tsdl.implementation.model.result.YieldStatement;
import org.tsdl.implementation.model.sample.TsdlSample;
import org.tsdl.implementation.parsing.TsdlQueryParser;
import org.tsdl.infrastructure.api.QueryService;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.common.TsdlUtil;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.infrastructure.model.TsdlLogEvent;
import org.tsdl.infrastructure.model.TsdlPeriod;
import org.tsdl.infrastructure.model.TsdlPeriodSet;

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

      final var logs = new ArrayList<TsdlLogEvent>();
      final var sampleValues = computeSamples(parsedQuery.samples(), data, logs);
      setSampleFilterArgumentValues(parsedQuery, sampleValues);

      var relevantDataPoints = parsedQuery.filter().isPresent()
          ? parsedQuery.filter().get().evaluateFilters(data)
          : data;

      // TODO optimization potential: if no events are defined, period detection is not necessary
      var detectedPeriods = detectPeriods(relevantDataPoints, parsedQuery);
      QueryResult result;
      if (parsedQuery.choice().isPresent()) {
        var periods = parsedQuery.choice().get().evaluate(detectedPeriods);
        result = getResult(periods, parsedQuery.result(), relevantDataPoints, sampleValues);
      } else {
        result = getResult(relevantDataPoints, parsedQuery.result(), sampleValues);
      }

      return result.withLogs(logs);
    } catch (TsdlEvaluationException e) {
      throw e;
    } catch (Exception e) {
      throw new TsdlEvaluationException("Query evaluation failed.", e);
    }
  }

  private QueryResult getResult(List<DataPoint> dataPoints, YieldStatement result, Map<TsdlIdentifier, Double> identifiers) {
    if (result.format() == YieldFormat.DATA_POINTS) {
      return QueryResult.of(dataPoints);
    }

    if (result.format() == YieldFormat.SAMPLE) {
      var sampleValue = identifiers.get(result.samples().get(0));
      return QueryResult.of(sampleValue);
    } else if (result.format() == YieldFormat.SAMPLE_SET) {
      var sampleValues = result.samples().stream().map(identifiers::get).toArray(Double[]::new);
      return QueryResult.of(sampleValues);
    }

    // since order preservation is part of the filter contract, we may assume the first element to be the start and the last element to be the end
    var periodStart = dataPoints.get(0).timestamp();
    var periodEnd = dataPoints.get(dataPoints.size() - 1).timestamp();
    var period = QueryResult.of(0, periodStart, periodEnd);

    if (result.format() == YieldFormat.LONGEST_PERIOD || result.format() == YieldFormat.SHORTEST_PERIOD) {
      return period;
    }

    if (result.format() == YieldFormat.ALL_PERIODS) {
      return QueryResult.of(1, List.of(period));
    }

    throw Conditions.exception(Condition.ARGUMENT, "Unknown result format '%s'", result);
  }

  private QueryResult getResult(TsdlPeriodSet periods, YieldStatement result, List<DataPoint> dataPoints, Map<TsdlIdentifier, Double> identifiers) {
    switch (result.format()) {
      case ALL_PERIODS:
        return periods;

      case LONGEST_PERIOD:
        return findSpecialPeriod(periods.periods(), true);

      case SHORTEST_PERIOD:
        return findSpecialPeriod(periods.periods(), false);

      case DATA_POINTS:
        var pointsInPeriods = dataPoints.stream()
            .filter(dp -> containsDataPoint(periods.periods(), dp.timestamp()))
            .toList();
        return QueryResult.of(pointsInPeriods);

      case SAMPLE:
        var sampleValue = identifiers.get(result.samples().get(0));
        return QueryResult.of(sampleValue);

      case SAMPLE_SET:
        var sampleValues = result.samples().stream().map(identifiers::get).toArray(Double[]::new);
        return QueryResult.of(sampleValues);

      default:
        throw Conditions.exception(Condition.ARGUMENT, "Unknown result format '%s'", result);
    }
  }

  private TsdlPeriod findSpecialPeriod(List<TsdlPeriod> periods, boolean longest) {
    var optimalDistance = longest ? Long.MIN_VALUE : Long.MAX_VALUE;
    //CHECKSTYLE.OFF: MatchXpath - false positive, 'var' cannot be used here (type 'BiFunction<Long, Long, Boolean>' cannot be inferred)
    BiPredicate<Long, Long> comparer = longest
        ? (newValue, currentOptimum) -> newValue > currentOptimum
        : (newValue, currentOptimum) -> newValue < currentOptimum;
    //CHECKSTYLE.ON: MatchXpath

    TsdlPeriod specialPeriod = null;
    for (var period : periods) {
      var distance = period.end().toEpochMilli() - period.start().toEpochMilli();
      if (comparer.test(distance, optimalDistance)) {
        optimalDistance = distance;
        specialPeriod = period;
      }
    }
    return specialPeriod != null ? specialPeriod : TsdlPeriod.EMPTY;
  }

  private boolean containsDataPoint(List<TsdlPeriod> periods, Instant timestamp) {
    return periods.stream()
        .anyMatch(period -> TsdlUtil.isWithinRange(timestamp, period.start(), period.end()));
  }

  /**
   * Precondition: SampleFilterArgument values have been set.
   * Postcondition: detected periods are ordered by start time; for equal start times, the period whose declaring event has the lower index is first
   */
  private List<AnnotatedTsdlPeriod> detectPeriods(List<DataPoint> dataPoints, TsdlQuery query) {
    var eventMarkers = new HashMap<TsdlIdentifier, Instant>();
    var detectedPeriods = new ArrayList<AnnotatedTsdlPeriod>();

    for (var i = 0; i < dataPoints.size(); i++) {
      var currentDataPoint = dataPoints.get(i);
      var previousDataPoint = i > 0 ? dataPoints.get(i - 1) : null;
      for (var event : query.events()) {
        markEvent(currentDataPoint, previousDataPoint, i == dataPoints.size() - 1, event, eventMarkers, detectedPeriods);
      }
    }

    return detectedPeriods;
  }

  private void markEvent(DataPoint currentDataPoint, DataPoint previousDataPoint, boolean isLastDataPoint, TsdlEvent event,
                         HashMap<TsdlIdentifier, Instant> eventMarkers, ArrayList<AnnotatedTsdlPeriod> detectedPeriods) {
    var eventId = event.identifier();
    if (event.definition().isSatisfied(currentDataPoint)) {
      // satisfied - either period is still going on or the period starts

      if (eventMarkers.containsKey(eventId)) {
        // period is still going on

        if (isLastDataPoint) {
          // if the end of the data is reached, the period must end, too
          finalizePeriod(detectedPeriods, eventMarkers, eventId, currentDataPoint.timestamp());
        }
      } else {
        // new period starts
        eventMarkers.put(eventId, currentDataPoint.timestamp());
      }

    } else {
      // not satisfied - either period ends, or there is still no open period

      //noinspection StatementWithEmptyBody
      if (eventMarkers.containsKey(eventId)) {
        // period ended
        Conditions.checkNotNull(Condition.ARGUMENT, previousDataPoint, "When closing a period, there must be a previous data point.");
        finalizePeriod(detectedPeriods, eventMarkers, eventId, previousDataPoint.timestamp());
      } else {
        // nothing to do - still no open period
      }
    }
  }

  private void finalizePeriod(List<AnnotatedTsdlPeriod> periods, Map<TsdlIdentifier, Instant> eventMarkers, TsdlIdentifier eventId,
                              Instant periodEnd) {
    var finalizedPeriod = QueryResult.of(null, eventMarkers.get(eventId), periodEnd);
    periods.add(new AnnotatedTsdlPeriodImpl(finalizedPeriod, eventId));
    eventMarkers.remove(eventId);
  }

  private Map<TsdlIdentifier, Double> computeSamples(List<TsdlSample> samples, List<DataPoint> input, List<TsdlLogEvent> logs) {
    return samples.stream().collect(Collectors.toMap(
            TsdlSample::identifier,
            sample -> sample.compute(input, logs)
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
            "Sample '%s' referenced by filter has not been computed. Is it declared in the 'SAMPLES' directive?".formatted(argumentIdentifier.name())
        );
      }

      filterArgument.setValue(sampleValues.get(argumentIdentifier));
    });
  }
}
