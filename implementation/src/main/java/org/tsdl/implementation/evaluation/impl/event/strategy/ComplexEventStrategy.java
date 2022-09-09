package org.tsdl.implementation.evaluation.impl.event.strategy;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.tsdl.implementation.evaluation.impl.choice.AnnotatedTsdlPeriodImpl;
import org.tsdl.implementation.factory.TsdlComponentFactory;
import org.tsdl.implementation.math.Calculus;
import org.tsdl.implementation.math.ContinuousRegression;
import org.tsdl.implementation.math.impl.ContinuousRegressionImpl;
import org.tsdl.implementation.model.choice.AnnotatedTsdlPeriod;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.event.TsdlEvent;
import org.tsdl.implementation.model.event.strategy.SinglePointEventStrategy;
import org.tsdl.implementation.model.event.strategy.TsdlEventStrategy;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.common.TsdlTimeUnit;
import org.tsdl.infrastructure.common.TsdlUtil;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.infrastructure.model.TsdlPeriod;

abstract class ComplexEventStrategy implements TsdlEventStrategy {
  protected static final Calculus CALCULUS = TsdlComponentFactory.INSTANCE.calculus();
  protected static final ContinuousRegression CONTINUOUS_REGRESSION = new ContinuousRegressionImpl();
  protected static final SinglePointEventStrategy EVENT_DETECTION_HELPER = new SinglePointEventStrategyImpl();

  protected List<AnnotatedTsdlPeriod> findPeriodCandidates(List<DataPoint> dataPoints, TsdlEvent event, TsdlIdentifier targetEventIdentifier) {
    return findPeriodCandidates(dataPoints, List.of(event), targetEventIdentifier);
  }

  protected List<AnnotatedTsdlPeriod> findPeriodCandidates(List<DataPoint> dataPoints, List<TsdlEvent> events, TsdlIdentifier targetEventIdentifier) {
    return replaceEventIdentifier(
        EVENT_DETECTION_HELPER
            .detectPeriods(dataPoints, events)
            .stream()
            .filter(p -> !p.period().isEmpty() && !p.period().start().equals(p.period().end())),
        targetEventIdentifier
    );
  }

  protected Map<TsdlPeriod, List<DataPoint>> groupDataPointsByPeriod(List<DataPoint> dataPoints, List<AnnotatedTsdlPeriod> periods) {
    var dpsPerPeriod = new HashMap<TsdlPeriod, List<DataPoint>>();
    for (var dp : dataPoints) {
      var currentPeriod = periods.stream().filter(p -> p.period().contains(dp.timestamp())).findFirst();
      currentPeriod.ifPresent(p -> dpsPerPeriod.computeIfAbsent(p.period(), (k) -> new ArrayList<>()).add(dp));
    }

    Conditions.checkEquals(Condition.ARGUMENT, dpsPerPeriod.size(), periods.size(), "Could find data points of every data period.");
    return dpsPerPeriod;
  }

  protected TsdlTimeUnit inferDerivativeUnit(Instant i0, Instant i1) {
    // order is important (descending "size")
    var timeUnits = new TsdlTimeUnit[] {TsdlTimeUnit.WEEKS, TsdlTimeUnit.DAYS, TsdlTimeUnit.HOURS, TsdlTimeUnit.MINUTES, TsdlTimeUnit.SECONDS,
        TsdlTimeUnit.MILLISECONDS};
    Conditions.checkSizeExactly(Condition.STATE, timeUnits, TsdlTimeUnit.values().length,
        "Method to infer time unit for derivative does not include all supported time units. Update implementation.");

    for (var unit : timeUnits) {
      var unitAdjustedSamplingRate = Math.abs(TsdlUtil.getTimespan(i0, i1, unit));
      if (unitAdjustedSamplingRate >= 0.85) {
        return unit;
      }
    }

    return TsdlTimeUnit.MILLISECONDS;
  }

  private List<AnnotatedTsdlPeriod> replaceEventIdentifier(Stream<AnnotatedTsdlPeriod> periods, TsdlIdentifier newEventIdentifier) {
    return periods
        .map(p -> (AnnotatedTsdlPeriod) new AnnotatedTsdlPeriodImpl(p.period(),
            newEventIdentifier,
            p.priorDataPoint().orElse(null),
            p.subsequentDataPoint().orElse(null)))
        .toList();
  }
}
