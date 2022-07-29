package org.tsdl.implementation.model.event;

import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

/**
 * Default implementation of {@link EventDuration}.
 */
public record EventDurationImpl(
    EventDurationBound lowerBound,
    EventDurationBound upperBound,
    EventDurationUnit unit
) implements EventDuration {

  public EventDurationImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, unit, "The unit of the event duration must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, lowerBound, "The lower bound of an event must not be null. Use 0 (inclusive) instead.");
    Conditions.checkNotNull(Condition.ARGUMENT, upperBound, "The upper bound of an event must not be null. Use Long.MAX_VALUE instead.");
  }
}
