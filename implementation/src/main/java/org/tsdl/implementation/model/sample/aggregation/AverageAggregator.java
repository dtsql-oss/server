package org.tsdl.implementation.model.sample.aggregation;

import org.tsdl.implementation.parsing.enums.AggregatorType;

/**
 * An aggregator calculating the arithmetic mean.
 */
public interface AverageAggregator extends TsdlAggregator {
  @Override
  default AggregatorType type() {
    return AggregatorType.AVERAGE;
  }
}
