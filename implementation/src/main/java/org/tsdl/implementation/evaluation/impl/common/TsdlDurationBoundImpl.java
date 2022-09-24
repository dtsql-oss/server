package org.tsdl.implementation.evaluation.impl.common;

import org.tsdl.implementation.model.common.TsdlDurationBound;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

/**
 * Default implementation of {@link TsdlDurationBound}.
 */
public record TsdlDurationBoundImpl(long value, boolean inclusive) implements TsdlDurationBound {
  public TsdlDurationBoundImpl {
    Conditions.checkIsGreaterThanOrEqual(Condition.ARGUMENT, value, 0L, "Duration bound value must be greater than or equal to 0.");
  }
}
