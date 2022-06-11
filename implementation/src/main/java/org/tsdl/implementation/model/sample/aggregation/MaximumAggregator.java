package org.tsdl.implementation.model.sample.aggregation;

import org.tsdl.implementation.parsing.enums.AggregatorType;

/**
 * An aggregator calculating the maximum.
 */
public interface MaximumAggregator extends TsdlAggregator {
  @Override
  default AggregatorType type() {
    return AggregatorType.MAXIMUM;
  }
}
