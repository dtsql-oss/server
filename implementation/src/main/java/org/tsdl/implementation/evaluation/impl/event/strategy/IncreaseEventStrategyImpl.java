package org.tsdl.implementation.evaluation.impl.event.strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;
import org.tsdl.implementation.evaluation.impl.choice.AnnotatedTsdlPeriodImpl;
import org.tsdl.implementation.evaluation.impl.common.TsdlIdentifierImpl;
import org.tsdl.implementation.evaluation.impl.event.TsdlEventImpl;
import org.tsdl.implementation.evaluation.impl.filter.NegatedSinglePointFilterImpl;
import org.tsdl.implementation.evaluation.impl.filter.argument.TsdlLiteralScalarArgumentImpl;
import org.tsdl.implementation.evaluation.impl.filter.threshold.LessThanFilterImpl;
import org.tsdl.implementation.factory.TsdlComponentFactory;
import org.tsdl.implementation.math.Calculus;
import org.tsdl.implementation.model.choice.AnnotatedTsdlPeriod;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.event.TsdlEvent;
import org.tsdl.implementation.model.event.TsdlEventStrategyType;
import org.tsdl.implementation.model.event.definition.AndEventConnectiveImpl;
import org.tsdl.implementation.model.event.definition.IncreaseEvent;
import org.tsdl.implementation.model.event.strategy.IncreaseEventStrategy;
import org.tsdl.implementation.model.event.strategy.SinglePointEventStrategy;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.common.TsdlTimeUnit;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.infrastructure.model.TsdlPeriod;

/**
 * Default implementation of {@link IncreaseEventStrategy}.
 */
public class IncreaseEventStrategyImpl implements IncreaseEventStrategy {
  private static final Calculus CALCULUS = TsdlComponentFactory.INSTANCE.calculus();
  private static final SinglePointEventStrategy EVENT_DETECTION_HELPER = new SinglePointEventStrategyImpl();

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

    var periodCandidates = replaceEvent(
        EVENT_DETECTION_HELPER
            .detectPeriods(derivative, List.of(derivateEvent))
            .stream()
            .filter(p -> !p.period().isEmpty() && !p.period().start().equals(p.period().end())),
        increaseEvent.identifier()
    );

    // get data points in period candidates (determine ps(p_i))
    var dpsPerPeriod = new HashMap<TsdlPeriod, List<DataPoint>>();
    for (var dp : dataPoints) {
      var currentPeriod = periodCandidates.stream().filter(p -> p.period().contains(dp.timestamp())).findFirst();
      currentPeriod.ifPresent(p -> dpsPerPeriod.computeIfAbsent(p.period(), (k) -> new ArrayList<>()).add(dp));
    }

    Conditions.checkEquals(Condition.ARGUMENT, dpsPerPeriod.size(), periodCandidates.size(), "Could find data points from every data period.");

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

  private List<AnnotatedTsdlPeriod> replaceEvent(Stream<AnnotatedTsdlPeriod> periods, TsdlIdentifier newEventIdentifier) {
    return periods
        .map(p -> (AnnotatedTsdlPeriod) new AnnotatedTsdlPeriodImpl(p.period(),
            newEventIdentifier,
            p.priorDataPoint().orElse(null),
            p.subsequentDataPoint().orElse(null)))
        .toList();
  }
}
