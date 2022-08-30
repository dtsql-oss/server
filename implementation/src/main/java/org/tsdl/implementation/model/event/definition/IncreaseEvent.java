package org.tsdl.implementation.model.event.definition;

import org.tsdl.implementation.model.event.TsdlEventStrategyType;

public interface IncreaseEvent extends MonotonicEvent {
  @Override
  default TsdlEventStrategyType computationStrategy() {
    return TsdlEventStrategyType.INCREASE_EVENT;
  }
}
