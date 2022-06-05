package org.tsdl.implementation.model.filter;

import org.tsdl.infrastructure.model.DataPoint;

/**
 * A filter that operates on a single data point. I.e. the decision whether a data point is eligible for the result set solely depends on its own
 * value.
 */
public interface SinglePointFilter {
  boolean evaluate(DataPoint dataPoint);
}
