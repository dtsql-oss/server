package org.tsdl.implementation.model.common;

import org.tsdl.implementation.evaluation.impl.common.TsdlDurationBoundImpl;

/**
 * Represents a (lower or upper) bound of a duration specification.
 */
public interface TsdlDurationBound {
  long value();

  boolean inclusive();

  static TsdlDurationBound of(long value, boolean inclusive) {
    return new TsdlDurationBoundImpl(value, inclusive);
  }
}
