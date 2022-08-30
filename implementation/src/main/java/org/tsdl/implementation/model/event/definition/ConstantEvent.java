package org.tsdl.implementation.model.event.definition;

import org.tsdl.implementation.model.event.TsdlEventStrategyType;
import org.tsdl.implementation.model.filter.argument.TsdlScalarArgument;

public interface ConstantEvent extends ComplexEventFunction {
  TsdlScalarArgument maximumSlope();

  TsdlScalarArgument maximumRelativeDeviation();

  @Override
  default TsdlEventStrategyType computationStrategy() {
    return TsdlEventStrategyType.CONSTANT_EVENT;
  }
}
