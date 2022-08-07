package org.tsdl.implementation.evaluation.impl.common;

import org.tsdl.implementation.model.common.ParsableTsdlTimeUnit;
import org.tsdl.implementation.model.common.TsdlDuration;
import org.tsdl.implementation.model.common.TsdlDurationBound;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

/**
 * Default implementation of {@link TsdlDuration}.
 */
public record TsdlDurationImpl(
    TsdlDurationBound lowerBound,
    TsdlDurationBound upperBound,
    ParsableTsdlTimeUnit unit
) implements TsdlDuration {

  public TsdlDurationImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, unit, "The unit of the event duration must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, lowerBound, "The lower bound of an event must not be null. Use 0 instead.");
    Conditions.checkNotNull(Condition.ARGUMENT, upperBound, "The upper bound of an event must not be null. Use Long.MAX_VALUE instead.");
  }
}
