package org.tsdl.implementation.model.event;

import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.connective.SinglePointFilterConnective;

/**
 * An event, as defined in a TSDL query.
 */
public interface TsdlEvent {
  SinglePointFilterConnective definition();

  TsdlIdentifier identifier();
}
