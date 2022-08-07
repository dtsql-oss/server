package org.tsdl.implementation.model.sample.aggregation.value;

import org.tsdl.implementation.parsing.enums.AggregatorType;

/**
 * An aggregator calculating the number of data point values.
 */
public interface CountAggregator extends ValueAggregator {
  @Override
  default AggregatorType type() {
    return AggregatorType.COUNT;
  }
}
