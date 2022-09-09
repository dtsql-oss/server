package org.tsdl.implementation.model.event.definition;

import org.tsdl.implementation.model.filter.argument.TsdlScalarArgument;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

/**
 * Default implementation of {@link DecreaseEvent}.
 */
public record DecreaseEventImpl(
    TsdlScalarArgument minimumChange,
    TsdlScalarArgument maximumChange,
    TsdlScalarArgument tolerance
) implements DecreaseEvent {
  public DecreaseEventImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, minimumChange, "Minimum change must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, maximumChange, "Maximum change must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, tolerance, "Tolerance must not be null.");
  }
}
