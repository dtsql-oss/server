package org.tsdl.implementation.evaluation.impl.event.strategy;

import java.util.ArrayList;
import java.util.List;
import org.tsdl.implementation.evaluation.impl.common.TsdlIdentifierImpl;
import org.tsdl.implementation.evaluation.impl.event.TsdlEventImpl;
import org.tsdl.implementation.evaluation.impl.filter.argument.TsdlLiteralScalarArgumentImpl;
import org.tsdl.implementation.evaluation.impl.filter.deviation.AbsoluteAroundFilterImpl;
import org.tsdl.implementation.math.impl.SummaryStatisticsImpl;
import org.tsdl.implementation.model.choice.AnnotatedTsdlPeriod;
import org.tsdl.implementation.model.event.TsdlEvent;
import org.tsdl.implementation.model.event.TsdlEventStrategyType;
import org.tsdl.implementation.model.event.definition.AndEventConnectiveImpl;
import org.tsdl.implementation.model.event.definition.ConstantEvent;
import org.tsdl.implementation.model.event.strategy.ConstantEventStrategy;
import org.tsdl.infrastructure.common.TsdlTimeUnit;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link ConstantEventStrategy}.
 */
public class ConstantEventStrategyImpl extends ComplexEventStrategy implements ConstantEventStrategy {
  private static final double DERIVATIVE_THRESHOLD = 0.002; // maximal instantaneous rate of change: 0.2 %

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
    var periodCandidates = findPeriodCandidates(derivative, derivativeEvent, constantEvent.identifier());
    var dpsPerPeriod = groupDataPointsByPeriod(dataPoints, periodCandidates);

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
}
