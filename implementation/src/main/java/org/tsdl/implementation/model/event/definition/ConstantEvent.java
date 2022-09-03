package org.tsdl.implementation.model.event.definition;

import org.tsdl.implementation.model.filter.argument.TsdlScalarArgument;

/**
 * A constant event.
 */
public interface ConstantEvent extends ComplexEventFunction {
  TsdlScalarArgument maximumSlope();

  TsdlScalarArgument maximumRelativeDeviation();
}
