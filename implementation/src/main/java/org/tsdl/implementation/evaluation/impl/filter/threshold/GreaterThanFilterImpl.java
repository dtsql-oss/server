package org.tsdl.implementation.evaluation.impl.filter.threshold;

import org.tsdl.implementation.model.filter.argument.TsdlScalarArgument;
import org.tsdl.implementation.model.filter.threshold.GreaterThanFilter;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link GreaterThanFilter}.
 */
public record GreaterThanFilterImpl(TsdlScalarArgument threshold) implements GreaterThanFilter {
  public GreaterThanFilterImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, threshold, "Threshold of 'greater than' filter must not be null.");
  }

  @Override
  public boolean evaluate(DataPoint dataPoint) {
    Conditions.checkNotNull(Condition.ARGUMENT, dataPoint, "Data point must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, dataPoint.value(), "Data point value must not be null.");

    return dataPoint.value() > threshold.value();
  }
}
