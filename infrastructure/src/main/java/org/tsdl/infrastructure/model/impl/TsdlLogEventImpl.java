package org.tsdl.infrastructure.model.impl;

import java.time.Instant;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.TsdlLogEvent;

/**
 * Default implementation of {@link TsdlLogEvent}.
 */
@Jacksonized
@Builder
@Value
@Accessors(fluent = true)
public class TsdlLogEventImpl implements TsdlLogEvent {
  Instant dateTime;
  String message;

  public TsdlLogEventImpl(Instant dateTime, String message) {
    Conditions.checkNotNull(Condition.ARGUMENT, dateTime, "Instant of log event must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, message, "Log message must not be null.");
    this.dateTime = dateTime;
    this.message = message;
  }
}
