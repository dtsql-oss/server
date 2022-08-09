package org.tsdl.implementation.model.sample.aggregation.temporal;

import org.tsdl.implementation.parsing.enums.AggregatorType;

/**
 * An aggregator calculating the arithmetic mean of period durations.
 */
public interface TemporalAverageAggregator extends TemporalAggregatorWithUnit {
  @Override
  default AggregatorType type() {
    return AggregatorType.TEMPORAL_AVERAGE;
  }
}
