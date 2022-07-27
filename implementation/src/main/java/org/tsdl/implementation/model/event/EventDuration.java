package org.tsdl.implementation.model.event;

/**
 * Represents the (preferred) duration of an event.
 */
public interface EventDuration {
  EventDurationBound lowerBound();

  EventDurationBound upperBound();

  EventDurationUnit unit();
}
