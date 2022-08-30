package org.tsdl.implementation.model.event.definition;

import org.tsdl.implementation.model.event.TsdlEventStrategyType;

public interface EventFunction {
  TsdlEventStrategyType computationStrategy();
}
