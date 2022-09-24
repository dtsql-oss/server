package org.tsdl.implementation.model.sample.aggregation.value;

import org.tsdl.implementation.parsing.enums.AggregatorType;

/**
 * An aggregator calculating the sum of data point values.
 */
public interface SumAggregator extends ValueAggregator {
  @Override
  default AggregatorType type() {
    return AggregatorType.SUM;
  }
}
