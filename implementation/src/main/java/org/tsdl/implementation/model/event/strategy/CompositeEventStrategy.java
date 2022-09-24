package org.tsdl.implementation.model.event.strategy;

/**
 * An {@link TsdlEventStrategy} that als employs another strategy instance in order to compute the periods specified by an event definition.
 */
public interface CompositeEventStrategy extends TsdlEventStrategy {
  TsdlEventStrategy strategy();
}
