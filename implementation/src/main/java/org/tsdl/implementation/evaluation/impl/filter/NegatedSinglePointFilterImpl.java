package org.tsdl.implementation.evaluation.impl.filter;

import org.tsdl.implementation.model.filter.NegatedSinglePointFilter;
import org.tsdl.implementation.model.filter.SinglePointFilter;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link NegatedSinglePointFilter}.
 */
public record NegatedSinglePointFilterImpl(SinglePointFilter filter) implements NegatedSinglePointFilter {
  public NegatedSinglePointFilterImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, filter, "Filter to be negated must not be null.");
  }

  @Override
  public boolean evaluate(DataPoint dataPoint) {
    return !filter.evaluate(dataPoint);
  }
}
