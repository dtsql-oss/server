package org.tsdl.infrastructure.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import org.tsdl.infrastructure.model.impl.TsdlLogEventImpl;


/**
 * A log event, typically induced by an output formatter triggered by an 'echo' instruction.
 */
@JsonDeserialize(as = TsdlLogEventImpl.class)
public interface TsdlLogEvent {
  Instant dateTime();

  String message();

  static TsdlLogEvent of(Instant dateTime, String message) {
    return new TsdlLogEventImpl(dateTime, message);
  }
}
