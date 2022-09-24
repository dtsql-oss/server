package org.tsdl.implementation.model.sample.aggregation.temporal;

import org.tsdl.implementation.parsing.enums.AggregatorType;

/**
 * An aggregator calculating the number of period durations.
 */
public interface TemporalCountAggregator extends TemporalAggregator {
  @Override
  default AggregatorType type() {
    return AggregatorType.TEMPORAL_COUNT;
  }
}
