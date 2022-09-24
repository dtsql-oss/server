package org.tsdl.implementation.model.event.definition;

import org.tsdl.implementation.model.filter.argument.TsdlScalarArgument;

/**
 * A monotonic event.
 */
public interface MonotonicEvent extends ComplexEventFunction {
  TsdlScalarArgument minimumChange();

  TsdlScalarArgument maximumChange();

  TsdlScalarArgument tolerance();
}
