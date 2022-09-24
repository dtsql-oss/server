package org.tsdl.implementation.evaluation.impl.filter.threshold;

import org.tsdl.implementation.model.filter.argument.TsdlScalarArgument;
import org.tsdl.implementation.model.filter.threshold.LessThanFilter;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link LessThanFilter}.
 */
public record LessThanFilterImpl(TsdlScalarArgument threshold) implements LessThanFilter {
  public LessThanFilterImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, threshold, "Threshold of 'less than' filter must not be null.");
  }

  @Override
  public boolean evaluate(DataPoint dataPoint) {
    Conditions.checkNotNull(Condition.ARGUMENT, dataPoint, "Data point must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, dataPoint.value(), "Data point value must not be null.");

    return dataPoint.value() < threshold.value();
  }
}
