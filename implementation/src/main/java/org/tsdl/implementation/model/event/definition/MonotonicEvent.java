package org.tsdl.implementation.model.event.definition;

import org.tsdl.implementation.model.filter.argument.TsdlScalarArgument;

public interface MonotonicEvent extends ComplexEventFunction {
  TsdlScalarArgument minimumChange();

  TsdlScalarArgument maximumChange();

  TsdlScalarArgument tolerance();
}
