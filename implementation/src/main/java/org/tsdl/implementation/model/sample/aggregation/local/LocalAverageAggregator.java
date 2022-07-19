package org.tsdl.implementation.model.sample.aggregation.local;

import org.tsdl.implementation.model.sample.aggregation.TsdlLocalAggregator;
import org.tsdl.implementation.parsing.enums.AggregatorType;

/**
 * An aggregator calculating the arithmetic in a given range.
 */
public interface LocalAverageAggregator extends TsdlLocalAggregator {
  @Override
  default AggregatorType type() {
    return AggregatorType.AVERAGE;
  }
}
