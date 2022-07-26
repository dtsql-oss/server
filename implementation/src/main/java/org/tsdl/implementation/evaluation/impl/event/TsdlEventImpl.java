package org.tsdl.implementation.evaluation.impl.event;

import org.tsdl.implementation.model.event.TsdlEvent;
import org.tsdl.implementation.model.event.TsdlEventStrategyType;
import org.tsdl.implementation.model.event.definition.TsdlEventDefinition;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

/**
 * Default implementation of {@link TsdlEvent}.
 */
public record TsdlEventImpl(TsdlEventDefinition definition, TsdlEventStrategyType computationStrategy) implements TsdlEvent {
  public TsdlEventImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, definition, "The defining filter connective of the event must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, computationStrategy, "The computation strategy of the event to be defined must not be null.");
  }
}
