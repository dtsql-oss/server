package org.tsdl.implementation.model.sample.aggregation;

import org.tsdl.implementation.parsing.enums.AggregatorType;

/**
 * An aggregator calculating the numeric integral of data point values.
 */
public interface IntegralAggregator extends TsdlAggregator {
  @Override
  default AggregatorType type() {
    return AggregatorType.INTEGRAL;
  }
}
