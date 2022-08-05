package org.tsdl.implementation.model.sample.aggregation;

import org.tsdl.implementation.parsing.enums.AggregatorType;

/**
 * An aggregator calculating the minimum of data point values.
 */
public interface MinimumAggregator extends SummaryAggregator {
  @Override
  default AggregatorType type() {
    return AggregatorType.MINIMUM;
  }
}
