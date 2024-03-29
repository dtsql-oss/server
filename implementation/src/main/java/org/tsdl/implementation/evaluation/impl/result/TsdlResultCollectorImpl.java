package org.tsdl.implementation.evaluation.impl.result;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import org.tsdl.implementation.evaluation.TsdlResultCollector;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.result.YieldStatement;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.infrastructure.model.TsdlLogEvent;
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
  public QueryResult collect(YieldStatement result, List<DataPoint> dataPoints, TsdlPeriodSet periodSet, boolean noPeriodDefinitions,
                             Map<TsdlIdentifier, Double> samples) {
    var indexedPeriodSet = normalizePeriodIndices(periodSet);
    switch (result.format()) {
      case ALL_PERIODS:
        return indexedPeriodSet;

      case LONGEST_PERIOD:
        return findSpecialPeriod(indexedPeriodSet.periods(), SpecialPeriod.MAXIMUM);

      case SHORTEST_PERIOD:
        return findSpecialPeriod(indexedPeriodSet.periods(), SpecialPeriod.MINIMUM);

      case DATA_POINTS:
        var pointsInPeriods = dataPoints.stream()
            .filter(dp -> noPeriodDefinitions || anyPeriodContains(indexedPeriodSet.periods(), dp.timestamp()))
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

  private TsdlPeriodSet normalizePeriodIndices(TsdlPeriodSet periodSet) {
    if (!periodSet.periods().stream().allMatch(period -> period.index() == -1)) {
      return periodSet;
    }

    var indexedPeriods = new ArrayList<TsdlPeriod>(periodSet.totalPeriods());
    var tsdlPeriods = periodSet.periods();
    for (var i = 0; i < tsdlPeriods.size(); i++) {
      var currentPeriod = tsdlPeriods.get(i);
      indexedPeriods.add(currentPeriod.withIndex(i));
    }

    return QueryResult.of(periodSet.totalPeriods(), indexedPeriods, periodSet.logs().toArray(TsdlLogEvent[]::new));
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
