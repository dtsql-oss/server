package org.tsdl.implementation.model.sample.aggregation.global;

import org.tsdl.implementation.model.sample.aggregation.TsdlGlobalAggregator;
import org.tsdl.implementation.parsing.enums.AggregatorType;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * An aggregator calculating the number of input {@link DataPoint} instances.
 */
public interface GlobalCountAggregator extends TsdlGlobalAggregator {
  @Override
  default AggregatorType type() {
    return AggregatorType.COUNT;
  }
}
