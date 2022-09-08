package org.tsdl.implementation.evaluation.impl.event.strategy;

import org.tsdl.implementation.evaluation.impl.filter.NegatedSinglePointFilterImpl;
import org.tsdl.implementation.evaluation.impl.filter.argument.TsdlLiteralScalarArgumentImpl;
import org.tsdl.implementation.evaluation.impl.filter.threshold.LessThanFilterImpl;
import org.tsdl.implementation.model.event.strategy.IncreaseEventStrategy;
import org.tsdl.implementation.model.filter.SinglePointFilter;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link IncreaseEventStrategy}.
 */
public class IncreaseEventStrategyImpl extends MonotonicEventStrategy implements IncreaseEventStrategy {
  @Override
  SinglePointFilter instantaneousRateOfChangeConstraint(double tolerance) {
    return new NegatedSinglePointFilterImpl(
        new LessThanFilterImpl(
            new TsdlLiteralScalarArgumentImpl(-(tolerance / 100.0))
        )
    );
  }

  @Override
  boolean relativeChangeConstraint(DataPoint startPoint, DataPoint endPoint, double minimumChange, double maximumChange) {
    var relativeChange = ((endPoint.value() - startPoint.value()) / Math.abs(startPoint.value())) * 100;
    return relativeChange >= 0 && relativeChange >= minimumChange && relativeChange <= maximumChange;
  }
}
