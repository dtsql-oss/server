package org.tsdl.implementation.model.sample.aggregation;

import org.tsdl.implementation.parsing.enums.AggregatorType;

/**
 * An aggregator calculating the sum of data point values.
 */
public interface SumAggregator extends TsdlAggregator {
  @Override
  default AggregatorType type() {
    return AggregatorType.SUM;
  }
}
