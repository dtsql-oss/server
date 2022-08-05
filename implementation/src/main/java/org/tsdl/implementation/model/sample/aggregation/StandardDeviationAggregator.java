package org.tsdl.implementation.model.sample.aggregation;

import org.tsdl.implementation.parsing.enums.AggregatorType;

/**
 * An aggregator calculating the population standard deviation of data point values.
 */
public interface StandardDeviationAggregator extends SummaryAggregator {
  @Override
  default AggregatorType type() {
    return AggregatorType.STANDARD_DEVIATION;
  }
}
