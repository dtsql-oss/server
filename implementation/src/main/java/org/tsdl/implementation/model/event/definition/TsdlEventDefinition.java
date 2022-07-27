package org.tsdl.implementation.model.event.definition;

import java.util.Optional;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.event.EventDuration;

/**
 * Definition of an event.
 */
public interface TsdlEventDefinition {
  TsdlIdentifier identifier();

  Optional<EventDuration> duration();
}
