package org.tsdl.implementation.model.sample.aggregation;

import org.tsdl.implementation.parsing.enums.AggregatorType;

/**
 * An aggregator calculating the minimum of data point values.
 */
public interface MinimumAggregator extends TsdlAggregator {
  @Override
  default AggregatorType type() {
    return AggregatorType.MINIMUM;
  }
}
