package org.tsdl.implementation.model.sample.aggregation;

import org.tsdl.implementation.parsing.enums.AggregatorType;

/**
 * An aggregator calculating the maximum of data point values.
 */
public interface MaximumAggregator extends TsdlAggregator {
  @Override
  default AggregatorType type() {
    return AggregatorType.MAXIMUM;
  }
}
