package org.tsdl.implementation.evaluation.impl.filter.deviation;

import org.tsdl.implementation.model.filter.argument.TsdlFilterArgument;
import org.tsdl.implementation.model.filter.deviation.AbsoluteAroundFilter;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link AbsoluteAroundFilter}.
 */
public record AbsoluteAroundFilterImpl(TsdlFilterArgument referenceValue, TsdlFilterArgument maximumDeviation) implements AbsoluteAroundFilter {
  public AbsoluteAroundFilterImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, referenceValue, "Reference value must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, maximumDeviation, "Maximum deviation must not be null.");
  }

  @Override
  public boolean evaluate(DataPoint dataPoint) {
    Conditions.checkNotNull(Condition.ARGUMENT, dataPoint, "Data point must not be null.");

    var absoluteDifference = Math.abs(dataPoint.value() - referenceValue.value());
    return absoluteDifference <= maximumDeviation.value();
  }
}
