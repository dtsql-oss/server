package org.tsdl.implementation.evaluation.impl.filter.deviation;

import org.tsdl.implementation.model.filter.argument.TsdlFilterArgument;
import org.tsdl.implementation.model.filter.deviation.RelativeAroundFilter;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link RelativeAroundFilter}.
 */
public record RelativeAroundFilterImpl(TsdlFilterArgument referenceValue, TsdlFilterArgument maximumDeviation) implements RelativeAroundFilter {
  public RelativeAroundFilterImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, referenceValue, "Reference value must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, maximumDeviation, "Maximum deviation must not be null.");
  }

  @Override
  public boolean evaluate(DataPoint dataPoint) {
    throw new UnsupportedOperationException();
  }
}
