package org.tsdl.implementation.model.sample.aggregation.local;

import org.tsdl.implementation.model.sample.aggregation.TsdlLocalAggregator;
import org.tsdl.implementation.parsing.enums.AggregatorType;

/**
 * An aggregator calculating the sum in a given range.
 */
public interface LocalSumAggregator extends TsdlLocalAggregator {
  @Override
  default AggregatorType type() {
    return AggregatorType.SUM;
  }
}
