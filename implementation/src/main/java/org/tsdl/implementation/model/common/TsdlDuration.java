package org.tsdl.implementation.model.common;

/**
 * Represents the (preferred) duration of an event.
 */
public interface TsdlDuration {
  TsdlDurationBound lowerBound();

  TsdlDurationBound upperBound();

  ParsableTsdlTimeUnit unit();
}
