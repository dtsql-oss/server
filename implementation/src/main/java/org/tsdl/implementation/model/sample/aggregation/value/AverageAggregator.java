package org.tsdl.implementation.model.sample.aggregation.value;

import org.tsdl.implementation.parsing.enums.AggregatorType;

/**
 * An aggregator calculating the arithmetic mean of data point values.
 */
public interface AverageAggregator extends ValueAggregator {
  @Override
  default AggregatorType type() {
    return AggregatorType.AVERAGE;
  }
}
