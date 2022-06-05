package org.tsdl.implementation.model.filter;

import org.tsdl.implementation.model.filter.argument.TsdlFilterArgument;

/**
 * A treshold single point filter.
 */
public interface ThresholdFilter extends SinglePointFilter {
  TsdlFilterArgument threshold();
}
