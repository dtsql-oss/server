package org.tsdl.infrastructure.model;

import java.time.Instant;
import org.tsdl.infrastructure.model.impl.TsdlLogEventImpl;

/**
 * A log event, typically induced by an outputformatter triggered by an 'echo' instruction.
 */
public interface TsdlLogEvent {
  Instant dateTime();

  String message();

  static TsdlLogEvent of(Instant dateTime, String message) {
    return new TsdlLogEventImpl(dateTime, message);
  }
}
