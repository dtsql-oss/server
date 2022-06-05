package org.tsdl.implementation.model.filter;

import org.tsdl.infrastructure.model.DataPoint;

/**
 * A special {@link SinglePointFilter} that returns the inverted result of a given {@link SinglePointFilter#evaluate(DataPoint)}} implementation.
 */
public interface NegatedSinglePointFilter extends SinglePointFilter {
  SinglePointFilter filter();
}
