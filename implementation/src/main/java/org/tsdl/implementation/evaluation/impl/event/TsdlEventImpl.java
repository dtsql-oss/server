package org.tsdl.implementation.evaluation.impl.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.connective.SinglePointFilterConnective;
import org.tsdl.implementation.model.event.TsdlEvent;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

/**
 * Default implementation of {@link TsdlEvent}.
 */
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode
@ToString
public final class TsdlEventImpl implements TsdlEvent {
  private final SinglePointFilterConnective definition;
  private final TsdlIdentifier identifier;

  public TsdlEventImpl(SinglePointFilterConnective definition, TsdlIdentifier identifier) {
    Conditions.checkNotNull(Condition.ARGUMENT, definition, "The defining filter connective of the event must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, identifier, "The identifier of the event to be defined must not be null.");
    this.definition = definition;
    this.identifier = identifier;
  }
}
