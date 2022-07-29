package org.tsdl.implementation.model.event.definition;

import org.tsdl.implementation.model.connective.SinglePointFilterConnective;

/**
 * Definition of a single point event.
 */
public interface SinglePointEventDefinition extends TsdlEventDefinition {
  SinglePointFilterConnective connective();
}
