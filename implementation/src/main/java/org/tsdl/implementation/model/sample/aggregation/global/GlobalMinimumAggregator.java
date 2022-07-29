package org.tsdl.implementation.model.sample.aggregation.global;

import org.tsdl.implementation.model.sample.aggregation.TsdlGlobalAggregator;
import org.tsdl.implementation.parsing.enums.AggregatorType;

/**
 * An aggregator calculating the minimum.
 */
public interface GlobalMinimumAggregator extends TsdlGlobalAggregator {
  @Override
  default AggregatorType type() {
    return AggregatorType.MINIMUM;
  }
}
