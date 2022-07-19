package org.tsdl.implementation.model.sample.aggregation.local;

import org.tsdl.implementation.model.sample.aggregation.TsdlLocalAggregator;
import org.tsdl.implementation.parsing.enums.AggregatorType;

/**
 * An aggregator calculating the maximum in a given range.
 */
public interface LocalMaximumAggregator extends TsdlLocalAggregator {
  @Override
  default AggregatorType type() {
    return AggregatorType.MAXIMUM;
  }
}
