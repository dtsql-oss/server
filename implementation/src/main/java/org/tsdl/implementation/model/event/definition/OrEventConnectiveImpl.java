package org.tsdl.implementation.model.event.definition;

import java.util.List;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

public record OrEventConnectiveImpl(List<EventFunction> events) implements AndEventConnective {
  public OrEventConnectiveImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, events, "Events must not be null.");
  }
}
