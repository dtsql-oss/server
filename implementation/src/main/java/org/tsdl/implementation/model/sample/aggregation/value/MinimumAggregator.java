package org.tsdl.implementation.model.sample.aggregation.value;

import org.tsdl.implementation.parsing.enums.AggregatorType;

/**
 * An aggregator calculating the minimum of data point values.
 */
public interface MinimumAggregator extends ValueAggregator {
  @Override
  default AggregatorType type() {
    return AggregatorType.MINIMUM;
  }
}
