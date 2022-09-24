package org.tsdl.implementation.evaluation.impl.event.strategy;

import org.tsdl.implementation.evaluation.impl.filter.NegatedSinglePointFilterImpl;
import org.tsdl.implementation.evaluation.impl.filter.argument.TsdlLiteralScalarArgumentImpl;
import org.tsdl.implementation.evaluation.impl.filter.threshold.GreaterThanFilterImpl;
import org.tsdl.implementation.model.event.strategy.DecreaseEventStrategy;
import org.tsdl.implementation.model.filter.SinglePointFilter;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link DecreaseEventStrategy}.
 */
public class DecreaseEventStrategyImpl extends MonotonicEventStrategy implements DecreaseEventStrategy {
  @Override
  SinglePointFilter instantaneousRateOfChangeConstraint(double tolerance) {
    return new NegatedSinglePointFilterImpl(
        new GreaterThanFilterImpl(
            new TsdlLiteralScalarArgumentImpl(tolerance / 100.0)
        )
    );
  }

  @Override
  boolean relativeChangeConstraint(DataPoint startPoint, DataPoint endPoint, double minimumChange, double maximumChange) {
    var relativeChange = ((endPoint.value() - startPoint.value()) / Math.abs(startPoint.value())) * 100;
    return relativeChange <= 0 && Math.abs(relativeChange) >= minimumChange && Math.abs(relativeChange) <= maximumChange;
  }
}
