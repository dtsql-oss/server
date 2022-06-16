package org.tsdl.implementation.model.sample.aggregation;

import org.tsdl.implementation.parsing.enums.AggregatorType;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * An aggregator calculating the number of input {@link DataPoint} instances.
 */
public interface CountAggregator extends TsdlAggregator {
  @Override
  default AggregatorType type() {
    return AggregatorType.COUNT;
  }
}
