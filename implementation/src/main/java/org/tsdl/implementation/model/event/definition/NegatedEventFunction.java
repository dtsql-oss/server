package org.tsdl.implementation.model.event.definition;

/**
 * A negated complex event function.
 */
public interface NegatedEventFunction extends ComplexEventFunction {
  EventFunction eventFunction();
}
