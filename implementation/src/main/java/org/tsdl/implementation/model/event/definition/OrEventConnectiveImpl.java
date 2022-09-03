package org.tsdl.implementation.model.event.definition;

import java.util.List;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

/**
 * Default implementation of {@link AndEventConnective}.
 */
public record OrEventConnectiveImpl(List<EventFunction> events) implements OrEventConnective {
  public OrEventConnectiveImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, events, "Events must not be null.");
  }
}
