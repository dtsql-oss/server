package org.tsdl.implementation.evaluation.impl.event;

import java.util.Optional;
import org.tsdl.implementation.model.common.TsdlDuration;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.event.TsdlEvent;
import org.tsdl.implementation.model.event.TsdlEventStrategyType;
import org.tsdl.implementation.model.event.definition.EventConnective;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

/**
 * Default implementation of {@link TsdlEvent}.
 */
public record TsdlEventImpl(
    EventConnective connective,
    TsdlIdentifier identifier,
    TsdlDuration durationValue,
    TsdlEventStrategyType computationStrategy
) implements TsdlEvent {
  public TsdlEventImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, connective, "The defining event connective must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, identifier, "The event identifier must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, computationStrategy, "The computation strategy of the event to be defined must not be null.");
  }

  @Override
  public String representation() {
    return identifier.representation();
  }

  @Override
  public Optional<TsdlDuration> duration() {
    return Optional.ofNullable(durationValue);
  }
}
