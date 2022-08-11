package org.tsdl.implementation.model.filter.deviation;

import org.tsdl.implementation.model.filter.argument.TsdlFilterArgument;

/**
 * A {@link DeviationFilter} that captures values that are within an implementation-specific corridor (deviation) around a reference value.
 */
public interface AroundFilter extends DeviationFilter {
  TsdlFilterArgument referenceValue();

  TsdlFilterArgument maximumDeviation();
}
