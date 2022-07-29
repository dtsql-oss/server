package org.tsdl.implementation.evaluation.impl.filter.threshold;

import org.tsdl.implementation.model.filter.threshold.LowerThanFilter;
import org.tsdl.implementation.model.filter.threshold.argument.TsdlFilterArgument;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link LowerThanFilter}.
 */
public record LowerThanFilterImpl(TsdlFilterArgument threshold) implements LowerThanFilter {
  public LowerThanFilterImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, threshold, "Threshold of 'lower than' filter must not be null.");
  }

  @Override
  public boolean evaluate(DataPoint dataPoint) {
    Conditions.checkNotNull(Condition.ARGUMENT, dataPoint, "Data point must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, dataPoint.value(), "Data point value must not be null.");

    return dataPoint.value() < threshold.value();
  }
}
