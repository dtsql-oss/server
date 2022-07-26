package org.tsdl.implementation.evaluation.impl.result;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import org.tsdl.implementation.evaluation.TsdlResultCollector;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.result.YieldFormat;
import org.tsdl.implementation.model.result.YieldStatement;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.infrastructure.model.TsdlPeriod;
import org.tsdl.infrastructure.model.TsdlPeriodSet;

/**
 * Default implementation of {@link TsdlResultCollector}.
 */
public class TsdlResultCollectorImpl implements TsdlResultCollector {
  private enum SpecialPeriod {
    MINIMUM, MAXIMUM
  }

  @Override
  public QueryResult collect(YieldStatement result, TsdlPeriodSet periods, List<DataPoint> dataPoints, Map<TsdlIdentifier, Double> samples) {
    switch (result.format()) {
      case ALL_PERIODS:
        return periods;

      case LONGEST_PERIOD:
        return findSpecialPeriod(periods.periods(), SpecialPeriod.MAXIMUM);

      case SHORTEST_PERIOD:
        return findSpecialPeriod(periods.periods(), SpecialPeriod.MINIMUM);

      case DATA_POINTS:
        var pointsInPeriods = dataPoints.stream()
            .filter(dp -> anyPeriodContains(periods.periods(), dp.timestamp()))
            .toList();
        return QueryResult.of(pointsInPeriods);

      case SAMPLE:
        var sampleValue = samples.get(result.samples().get(0));
        return QueryResult.of(sampleValue);

      case SAMPLE_SET:
        var sampleValues = result.samples().stream().map(samples::get).toArray(Double[]::new);
        return QueryResult.of(sampleValues);

      default:
        throw Conditions.exception(Condition.ARGUMENT, "Unknown result format '%s'", result);
    }
  }

  @Override
  public QueryResult collect(YieldStatement result, List<DataPoint> dataPoints, Map<TsdlIdentifier, Double> samples) {
    if (result.format() == YieldFormat.DATA_POINTS) {
      return QueryResult.of(dataPoints);
    }

    if (result.format() == YieldFormat.SAMPLE) {
      var sampleValue = samples.get(result.samples().get(0));
      return QueryResult.of(sampleValue);
    } else if (result.format() == YieldFormat.SAMPLE_SET) {
      var sampleValues = result.samples().stream().map(samples::get).toArray(Double[]::new);
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

  private TsdlPeriod findSpecialPeriod(List<TsdlPeriod> periods, SpecialPeriod type) {
    var optimalDistance = type == SpecialPeriod.MAXIMUM ? Long.MIN_VALUE : Long.MAX_VALUE;
    //CHECKSTYLE.OFF: MatchXpath - false positive, 'var' cannot be used here (type 'BiFunction<Long, Long, Boolean>' cannot be inferred)
    BiPredicate<Long, Long> comparer = type == SpecialPeriod.MAXIMUM
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

  private boolean anyPeriodContains(List<TsdlPeriod> periods, Instant timestamp) {
    return periods.stream()
        .anyMatch(period -> period.contains(timestamp));
  }
}
