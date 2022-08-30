package org.tsdl.implementation.model.filter;

import org.tsdl.implementation.model.event.TsdlEventStrategyType;
import org.tsdl.implementation.model.event.definition.EventFunction;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * A filter that operates on a single data point. I.e. the decision whether a data point is eligible for the result set solely depends on its own
 * value.
 */
public interface SinglePointFilter extends EventFunction {
  boolean evaluate(DataPoint dataPoint);

  @Override
  default TsdlEventStrategyType computationStrategy() {
    return TsdlEventStrategyType.SINGLE_POINT_EVENT;
  }
}
