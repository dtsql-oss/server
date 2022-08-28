package org.tsdl.implementation.model.filter.deviation;

import org.tsdl.implementation.model.filter.argument.TsdlScalarArgument;

/**
 * A {@link DeviationFilter} that captures values that are within an implementation-specific corridor (deviation) around a reference value.
 */
public interface AroundFilter extends DeviationFilter {
  TsdlScalarArgument referenceValue();

  TsdlScalarArgument maximumDeviation();
}
