package org.tsdl.implementation.model.sample.aggregation.global;

import org.tsdl.implementation.model.sample.aggregation.TsdlGlobalAggregator;
import org.tsdl.implementation.parsing.enums.AggregatorType;

/**
 * An aggregator calculating the arithmetic mean.
 */
public interface GlobalAverageAggregator extends TsdlGlobalAggregator {
  @Override
  default AggregatorType type() {
    return AggregatorType.AVERAGE;
  }
}
