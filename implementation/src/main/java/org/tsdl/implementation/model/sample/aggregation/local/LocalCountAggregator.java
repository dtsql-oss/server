package org.tsdl.implementation.model.sample.aggregation.local;

import org.tsdl.implementation.model.sample.aggregation.TsdlLocalAggregator;
import org.tsdl.implementation.parsing.enums.AggregatorType;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * An aggregator calculating the number of input {@link DataPoint} instances in a given range.
 */
public interface LocalCountAggregator extends TsdlLocalAggregator {
  @Override
  default AggregatorType type() {
    return AggregatorType.COUNT;
  }
}
