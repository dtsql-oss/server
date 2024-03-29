package org.tsdl.implementation.model.filter.threshold;

import org.tsdl.implementation.model.filter.SinglePointFilter;
import org.tsdl.implementation.model.filter.argument.TsdlScalarArgument;

/**
 * A threshold single point filter, i.e., the eligibility of a data point for the result set depends on the value component.
 */
public interface ThresholdFilter extends SinglePointFilter {
  TsdlScalarArgument threshold();
}
