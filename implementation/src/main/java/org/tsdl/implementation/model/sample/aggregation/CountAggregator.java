package org.tsdl.implementation.model.sample.aggregation;

import org.tsdl.implementation.parsing.enums.AggregatorType;

/**
 * An aggregator calculating the number of data point values.
 */
public interface CountAggregator extends TsdlAggregator {
  @Override
  default AggregatorType type() {
    return AggregatorType.COUNT;
  }
}
