package org.tsdl.implementation.model.event;

import org.tsdl.implementation.model.common.ParsableTsdlTimeUnit;

/**
 * Represents the (preferred) duration of an event.
 */
public interface EventDuration {
  EventDurationBound lowerBound();

  EventDurationBound upperBound();

  ParsableTsdlTimeUnit unit();
}
