package org.tsdl.implementation.evaluation.impl.event.definition;

import java.util.Optional;
import org.tsdl.implementation.model.common.TsdlDuration;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.connective.SinglePointFilterConnective;
import org.tsdl.implementation.model.event.definition.SinglePointEventDefinition;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

/**
 * Default implementation of {@link SinglePointEventDefinition}.
 */
public record SinglePointEventDefinitionImpl(
    TsdlIdentifier identifier,
    SinglePointFilterConnective connective,
    TsdlDuration durationValue
) implements SinglePointEventDefinition {
  public SinglePointEventDefinitionImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, identifier, "The identifier of the event to be defined must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, connective, "The connective must not be null.");
  }

  @Override
  public Optional<TsdlDuration> duration() {
    return Optional.ofNullable(durationValue);
  }
}
