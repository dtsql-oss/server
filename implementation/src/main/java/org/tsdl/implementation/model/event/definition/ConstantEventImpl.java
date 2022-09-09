package org.tsdl.implementation.model.event.definition;

import org.tsdl.implementation.model.filter.argument.TsdlScalarArgument;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

/**
 * Default implementation of {@link ConstantEvent}.
 */
public record ConstantEventImpl(
    TsdlScalarArgument maximumSlope,
    TsdlScalarArgument maximumRelativeDeviation
) implements ConstantEvent {
  public ConstantEventImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, maximumSlope, "Maximum slope argument must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, maximumRelativeDeviation, "Maximum relative deviation argument must not be null.");
  }
}
