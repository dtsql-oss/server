package org.tsdl.implementation.model.event;

import java.util.Optional;
import org.tsdl.implementation.model.choice.relation.TemporalOperand;
import org.tsdl.implementation.model.common.TsdlDuration;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.event.definition.EventConnective;

/**
 * An event, as defined in a TSDL query.
 */
public interface TsdlEvent extends TemporalOperand {
  EventConnective connective();

  TsdlIdentifier identifier();

  Optional<TsdlDuration> duration();
}
