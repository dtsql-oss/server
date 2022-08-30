package org.tsdl.implementation.model.event.definition;

import org.tsdl.implementation.model.event.TsdlEventStrategyType;

public interface DecreaseEvent extends MonotonicEvent {
  @Override
  default TsdlEventStrategyType computationStrategy() {
    return TsdlEventStrategyType.DECREASE_EVENT;
  }
}
