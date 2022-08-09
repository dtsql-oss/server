package org.tsdl.implementation.model.common;

/**
 * Represents the (preferred) duration of an event.
 */
public interface TsdlDuration {
  TsdlDurationBound lowerBound();

  TsdlDurationBound upperBound();

  ParsableTsdlTimeUnit unit();

  /**
   * <p>
   * Precondition: {@code value} is in unit {@link #unit()}
   * </p>
   * <p>
   * Determines whether a given {@code value} is in the range/duration given represented by this {@link TsdlDuration} instance.
   * </p>
   *
   * @return true if {@code value} satisfies the bounds of this instance, otherwise false
   */
  boolean isSatisfiedBy(double unitAdjustedValue);
}
