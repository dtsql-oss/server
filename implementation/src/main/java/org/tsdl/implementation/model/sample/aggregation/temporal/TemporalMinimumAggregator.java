package org.tsdl.implementation.model.sample.aggregation.temporal;

import org.tsdl.implementation.parsing.enums.AggregatorType;

/**
 * An aggregator calculating the minimum of period durations.
 */
public interface TemporalMinimumAggregator extends TemporalAggregator {
  @Override
  default AggregatorType type() {
    return AggregatorType.TEMPORAL_MINIMUM;
  }
}
