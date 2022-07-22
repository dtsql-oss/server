package org.tsdl.implementation.evaluation.impl.filter.temporal;

import java.time.Instant;
import org.tsdl.implementation.model.filter.temporal.BeforeFilter;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link BeforeFilter}.
 */
public record BeforeFilterImpl(Instant argument) implements BeforeFilter {
  public BeforeFilterImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, argument, "Argument of 'before' filter must not be null.");
  }

  @Override
  public boolean evaluate(DataPoint dataPoint) {
    Conditions.checkNotNull(Condition.ARGUMENT, dataPoint, "Data point must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, dataPoint.timestamp(), "Data point timestamp must not be null.");

    return dataPoint.timestamp().isBefore(argument);
  }
}
