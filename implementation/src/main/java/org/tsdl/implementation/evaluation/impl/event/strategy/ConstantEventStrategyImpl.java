package org.tsdl.implementation.evaluation.impl.event.strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;
import org.tsdl.implementation.evaluation.impl.choice.AnnotatedTsdlPeriodImpl;
import org.tsdl.implementation.evaluation.impl.common.TsdlIdentifierImpl;
import org.tsdl.implementation.evaluation.impl.event.TsdlEventImpl;
import org.tsdl.implementation.evaluation.impl.filter.argument.TsdlLiteralScalarArgumentImpl;
import org.tsdl.implementation.evaluation.impl.filter.deviation.AbsoluteAroundFilterImpl;
import org.tsdl.implementation.factory.TsdlComponentFactory;
import org.tsdl.implementation.math.Calculus;
import org.tsdl.implementation.math.ContinuousRegression;
import org.tsdl.implementation.math.impl.ContinuousRegressionImpl;
import org.tsdl.implementation.math.impl.SummaryStatisticsImpl;
import org.tsdl.implementation.model.choice.AnnotatedTsdlPeriod;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.event.TsdlEvent;
import org.tsdl.implementation.model.event.TsdlEventStrategyType;
import org.tsdl.implementation.model.event.definition.AndEventConnectiveImpl;
import org.tsdl.implementation.model.event.definition.ConstantEvent;
import org.tsdl.implementation.model.event.strategy.ConstantEventStrategy;
import org.tsdl.implementation.model.event.strategy.SinglePointEventStrategy;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.common.TsdlTimeUnit;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.infrastructure.model.TsdlPeriod;

/**
 * Default implementation of {@link ConstantEventStrategy}.
 */
public class ConstantEventStrategyImpl implements ConstantEventStrategy {
  private static final Calculus CALCULUS = TsdlComponentFactory.INSTANCE.calculus();
  private static final ContinuousRegression CONTINUOUS_REGRESSION = new ContinuousRegressionImpl();
  private static final double DERIVATIVE_THRESHOLD = 0.002; // maximal instantaneous rate of change: 0.2 %
  private static final SinglePointEventStrategy EVENT_DETECTION_HELPER = new SinglePointEventStrategyImpl();

  @Override
  public List<AnnotatedTsdlPeriod> detectPeriods(List<DataPoint> dataPoints, List<TsdlEvent> events) {
    var constantEvent = events.get(0);
    var constantEventFunction = (ConstantEvent) constantEvent.connective().events().get(0);
    var derivative = CALCULUS.derivative(dataPoints, TsdlTimeUnit.SECONDS);

    var derivativeEvent = new TsdlEventImpl(
        new AndEventConnectiveImpl(
            List.of(
                new AbsoluteAroundFilterImpl(
                    new TsdlLiteralScalarArgumentImpl(0.0),
                    new TsdlLiteralScalarArgumentImpl(DERIVATIVE_THRESHOLD)
                )
            )
        ),
        new TsdlIdentifierImpl("helper"),
        null,
        TsdlEventStrategyType.SINGLE_POINT_EVENT
    );

    // heuristic
    var periodCandidates = replaceEvent(
        EVENT_DETECTION_HELPER
            .detectPeriods(derivative, List.of(derivativeEvent))
            .stream()
            .filter(p -> !p.period().isEmpty() && !p.period().start().equals(p.period().end())),
        constantEvent.identifier()
    );

    // get data points in period candidates (determine ps(p_i))
    var dpsPerPeriod = new HashMap<TsdlPeriod, List<DataPoint>>();
    for (var dp : dataPoints) {
      var currentPeriod = periodCandidates.stream().filter(p -> p.period().contains(dp.timestamp())).findFirst();
      currentPeriod.ifPresent(p -> dpsPerPeriod.computeIfAbsent(p.period(), (k) -> new ArrayList<>()).add(dp));
    }

    Conditions.checkEquals(Condition.ARGUMENT, dpsPerPeriod.size(), periodCandidates.size(), "Could find data points from every data period.");

    // now semantics definition to filter out invalid periods

    // regc
    var satRegc = new ArrayList<AnnotatedTsdlPeriod>();
    for (var candidate : periodCandidates) {
      var dps = dpsPerPeriod.get(candidate.period());
      var regressionLine = CONTINUOUS_REGRESSION.linearLeastSquares(dps);
      if (Math.abs(regressionLine.slope()) * 100 <= constantEventFunction.maximumSlope().value()) {
        satRegc.add(candidate);
      }
    }

    // devc
    var satDevc = new ArrayList<AnnotatedTsdlPeriod>();
    for (var annotatedTsdlPeriod : satRegc) {
      var dps = dpsPerPeriod.get(annotatedTsdlPeriod.period());
      var stats = new SummaryStatisticsImpl();
      stats.ingest(() -> dps.stream().map(DataPoint::value).toList());
      var avg = stats.average();
      var sat = dps.stream().allMatch(x -> {
        var absoluteDifference = Math.abs(x.value() - avg);
        var percentageDifference = (absoluteDifference / Math.abs(avg)) * 100;
        return percentageDifference <= constantEventFunction.maximumRelativeDeviation().value();
      });
      if (sat) {
        satDevc.add(annotatedTsdlPeriod);
      }
    }

    return satDevc;
  }

  private List<AnnotatedTsdlPeriod> replaceEvent(Stream<AnnotatedTsdlPeriod> periods, TsdlIdentifier newEventIdentifier) {
    return periods
        .map(p -> (AnnotatedTsdlPeriod) new AnnotatedTsdlPeriodImpl(p.period(),
            newEventIdentifier,
            p.priorDataPoint().orElse(null),
            p.subsequentDataPoint().orElse(null)))
        .toList();
  }
}
