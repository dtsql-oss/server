package org.tsdl.implementation.model.event;

import org.tsdl.implementation.model.event.definition.TsdlEventDefinition;

/**
 * An event, as defined in a TSDL query.
 */
public interface TsdlEvent {
  TsdlEventDefinition definition();

  TsdlEventStrategyType computationStrategy();
}
