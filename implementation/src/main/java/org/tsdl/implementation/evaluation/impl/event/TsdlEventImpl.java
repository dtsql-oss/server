package org.tsdl.implementation.evaluation.impl.event;

import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.connective.SinglePointFilterConnective;
import org.tsdl.implementation.model.event.TsdlEvent;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

/**
 * Default implementation of {@link TsdlEvent}.
 */
public record TsdlEventImpl(SinglePointFilterConnective definition, TsdlIdentifier identifier) implements TsdlEvent {
  public TsdlEventImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, definition, "The defining filter connective of the event must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, identifier, "The identifier of the event to be defined must not be null.");
  }
}
