package org.tsdl.implementation.evaluation.impl.event.strategy;

import java.util.ArrayList;
import java.util.List;
import org.tsdl.implementation.evaluation.impl.common.TsdlIdentifierImpl;
import org.tsdl.implementation.evaluation.impl.event.TsdlEventImpl;
import org.tsdl.implementation.model.choice.AnnotatedTsdlPeriod;
import org.tsdl.implementation.model.event.TsdlEvent;
import org.tsdl.implementation.model.event.TsdlEventStrategyType;
import org.tsdl.implementation.model.event.definition.AndEventConnectiveImpl;
import org.tsdl.implementation.model.event.definition.MonotonicEvent;
import org.tsdl.implementation.model.filter.SinglePointFilter;
import org.tsdl.infrastructure.common.TsdlTimeUnit;
import org.tsdl.infrastructure.model.DataPoint;

abstract class MonotonicEventStrategy extends ComplexEventStrategy {
  abstract SinglePointFilter instantaneousRateOfChangeConstraint(double tolerance);

  abstract boolean relativeChangeConstraint(DataPoint startPoint, DataPoint endPoint, double minimumChange, double maximumChange);

  @Override
  public List<AnnotatedTsdlPeriod> detectPeriods(List<DataPoint> dataPoints, List<TsdlEvent> events) {
    var monotonicEvent = events.get(0);
    var monotonicEventFunction = ((MonotonicEvent) monotonicEvent.connective().events().get(0));
    var derivative = CALCULUS.derivative(dataPoints, TsdlTimeUnit.SECONDS);

    // ratc
    var derivateEvent = new TsdlEventImpl(
        new AndEventConnectiveImpl(
            List.of(
                instantaneousRateOfChangeConstraint(monotonicEventFunction.tolerance().value())
            )
        ),
        new TsdlIdentifierImpl("helper"),
        null,
        TsdlEventStrategyType.SINGLE_POINT_EVENT
    );

    var periodCandidates = findPeriodCandidates(derivative, derivateEvent, monotonicEvent.identifier());
    var dpsPerPeriod = groupDataPointsByPeriod(dataPoints, periodCandidates);

    // difc
    var difc = new ArrayList<AnnotatedTsdlPeriod>();
    for (var candidate : periodCandidates) {
      var dps = dpsPerPeriod.get(candidate.period());
      var startPoint = dps.get(0);
      var endPoint = dps.get(dps.size() - 1);

      if (relativeChangeConstraint(startPoint, endPoint, monotonicEventFunction.minimumChange().value(),
          monotonicEventFunction.maximumChange().value())) {
        difc.add(candidate);
      }
    }

    return difc;
  }
}
