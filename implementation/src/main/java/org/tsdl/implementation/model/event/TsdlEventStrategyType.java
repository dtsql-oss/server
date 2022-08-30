package org.tsdl.implementation.model.event;

import org.tsdl.implementation.model.event.strategy.TsdlEventStrategy;

/**
 * Identifies different event computation algorithms, represented by {@link TsdlEventStrategy} implementations.
 */
public enum TsdlEventStrategyType {
  SINGLE_POINT_EVENT, SINGLE_POINT_EVENT_WITH_DURATION, CONSTANT_EVENT, INCREASE_EVENT, DECREASE_EVENT
}
