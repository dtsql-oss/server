package org.tsdl.implementation.model.event;

import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

/**
 * Default implementation of {@link EventDurationBound}.
 */
public record EventDurationBoundImpl(long value, boolean inclusive) implements EventDurationBound {
  public EventDurationBoundImpl {
    Conditions.checkIsGreaterThanOrEqual(Condition.ARGUMENT, value, 0L, "Duration bound value must be greater than or equal to 0.");
  }
}
