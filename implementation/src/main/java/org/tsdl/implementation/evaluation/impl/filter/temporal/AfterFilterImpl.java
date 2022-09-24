package org.tsdl.implementation.evaluation.impl.filter.temporal;

import java.time.Instant;
import org.tsdl.implementation.model.filter.temporal.AfterFilter;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link AfterFilter}.
 */
public record AfterFilterImpl(Instant argument) implements AfterFilter {
  public AfterFilterImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, argument, "Argument of 'after' filter must not be null.");
  }

  @Override
  public boolean evaluate(DataPoint dataPoint) {
    Conditions.checkNotNull(Condition.ARGUMENT, dataPoint, "Data point must not be null.");
    return dataPoint.timestamp().isAfter(argument);
  }
}
