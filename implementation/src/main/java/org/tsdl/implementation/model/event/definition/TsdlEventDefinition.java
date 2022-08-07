package org.tsdl.implementation.model.event.definition;

import java.util.Optional;
import org.tsdl.implementation.model.common.TsdlDuration;
import org.tsdl.implementation.model.common.TsdlIdentifier;

/**
 * Definition of an event.
 */
public interface TsdlEventDefinition {
  TsdlIdentifier identifier();

  Optional<TsdlDuration> duration();
}
