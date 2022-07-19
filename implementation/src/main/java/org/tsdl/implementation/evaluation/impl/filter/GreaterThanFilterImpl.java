package org.tsdl.implementation.evaluation.impl.filter;

import org.tsdl.implementation.model.filter.GreaterThanFilter;
import org.tsdl.implementation.model.filter.argument.TsdlFilterArgument;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link GreaterThanFilter}.
 */
public record GreaterThanFilterImpl(TsdlFilterArgument threshold) implements GreaterThanFilter {
  public GreaterThanFilterImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, threshold, "Threshold of 'greater than' filter must not be null.");
  }

  @Override
  public boolean evaluate(DataPoint dataPoint) {
    Conditions.checkNotNull(Condition.ARGUMENT, dataPoint, "Data point must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, dataPoint.value(), "Data point value must not be null.");
    Conditions.checkNotNull(Condition.STATE, threshold, "Threshold must not be null.");

    return dataPoint.value() > threshold.value();
  }
}
