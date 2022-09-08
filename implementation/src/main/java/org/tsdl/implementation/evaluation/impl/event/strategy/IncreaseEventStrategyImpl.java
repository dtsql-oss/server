package org.tsdl.implementation.evaluation.impl.event.strategy;

import java.util.ArrayList;
import java.util.List;
import org.tsdl.implementation.evaluation.impl.common.TsdlIdentifierImpl;
import org.tsdl.implementation.evaluation.impl.event.TsdlEventImpl;
import org.tsdl.implementation.evaluation.impl.filter.NegatedSinglePointFilterImpl;
import org.tsdl.implementation.evaluation.impl.filter.argument.TsdlLiteralScalarArgumentImpl;
import org.tsdl.implementation.evaluation.impl.filter.threshold.LessThanFilterImpl;
import org.tsdl.implementation.model.choice.AnnotatedTsdlPeriod;
import org.tsdl.implementation.model.event.TsdlEvent;
import org.tsdl.implementation.model.event.TsdlEventStrategyType;
import org.tsdl.implementation.model.event.definition.AndEventConnectiveImpl;
import org.tsdl.implementation.model.event.definition.IncreaseEvent;
import org.tsdl.implementation.model.event.strategy.IncreaseEventStrategy;
import org.tsdl.infrastructure.common.TsdlTimeUnit;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link IncreaseEventStrategy}.
 */
public class IncreaseEventStrategyImpl extends ComplexEventStrategy implements IncreaseEventStrategy {
  @Override
  public List<AnnotatedTsdlPeriod> detectPeriods(List<DataPoint> dataPoints, List<TsdlEvent> events) {
    var increaseEvent = events.get(0);
    var increaseEventFunction = ((IncreaseEvent) increaseEvent.connective().events().get(0));
    var derivative = CALCULUS.derivative(dataPoints, TsdlTimeUnit.SECONDS);

    // ratc
    var derivateEvent = new TsdlEventImpl(
        new AndEventConnectiveImpl(
            List.of(
                new NegatedSinglePointFilterImpl(
                    new LessThanFilterImpl(
                        new TsdlLiteralScalarArgumentImpl(-(increaseEventFunction.tolerance().value() / 100.0))
                    )
                )
            )
        ),
        new TsdlIdentifierImpl("helper"),
        null,
        TsdlEventStrategyType.SINGLE_POINT_EVENT
    );

    var periodCandidates = findPeriodCandidates(derivative, derivateEvent, increaseEvent.identifier());
    var dpsPerPeriod = groupDataPointsByPeriod(dataPoints, periodCandidates);

    // difc
    var difc = new ArrayList<AnnotatedTsdlPeriod>();
    for (var candidate : periodCandidates) {
      var dps = dpsPerPeriod.get(candidate.period());
      var startPoint = dps.get(0);
      var endPoint = dps.get(dps.size() - 1);

      var relativeChange = ((endPoint.value() - startPoint.value()) / Math.abs(startPoint.value())) * 100;
      if (relativeChange >= 0 && relativeChange >= increaseEventFunction.minimumChange().value()
          && relativeChange <= increaseEventFunction.maximumChange().value()) {
        difc.add(candidate);
      }
    }

    return difc;
  }
}
