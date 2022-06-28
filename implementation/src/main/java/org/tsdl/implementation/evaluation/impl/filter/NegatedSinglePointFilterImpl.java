package org.tsdl.implementation.evaluation.impl.filter;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.tsdl.implementation.model.filter.NegatedSinglePointFilter;
import org.tsdl.implementation.model.filter.SinglePointFilter;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link NegatedSinglePointFilter}.
 */
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode
@ToString
public final class NegatedSinglePointFilterImpl implements NegatedSinglePointFilter {
  private final SinglePointFilter filter;

  public NegatedSinglePointFilterImpl(SinglePointFilter filter) {
    Conditions.checkNotNull(Condition.ARGUMENT, filter, "Filter to be negated must not be null.");
    this.filter = filter;
  }

  @Override
  public boolean evaluate(DataPoint dataPoint) {
    return !filter.evaluate(dataPoint);
  }
}
