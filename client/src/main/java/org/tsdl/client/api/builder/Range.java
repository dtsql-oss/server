package org.tsdl.client.api.builder;

import java.util.Optional;
import org.tsdl.infrastructure.common.TsdlTimeUnit;

/**
 * Represents a range (or an interval) with a time unit to be used in a TSDL query.
 */
public interface Range {
  /**
   * Defines whether bounds are inclusive (closed) or exclusive (open).
   */
  enum IntervalType {
    OPEN_START, OPEN_END, CLOSED, OPEN
  }

  Optional<Long> lowerBound();

  Optional<Long> upperBound();

  TsdlTimeUnit unit();

  IntervalType type();
}
