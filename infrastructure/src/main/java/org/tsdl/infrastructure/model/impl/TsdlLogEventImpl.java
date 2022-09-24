package org.tsdl.infrastructure.model.impl;

import java.time.Instant;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.TsdlLogEvent;

/**
 * Default implementation of {@link TsdlLogEvent}.
 */
public record TsdlLogEventImpl(Instant dateTime, String message) implements TsdlLogEvent {
  public TsdlLogEventImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, dateTime, "Instant of log event must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, message, "Log message must not be null.");
  }
}
