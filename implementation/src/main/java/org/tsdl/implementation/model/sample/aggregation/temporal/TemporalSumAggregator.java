package org.tsdl.implementation.model.sample.aggregation.temporal;

import org.tsdl.implementation.parsing.enums.AggregatorType;

/**
 * An aggregator calculating the sum of period durations.
 */
public interface TemporalSumAggregator extends TemporalAggregatorWithUnit {
  @Override
  default AggregatorType type() {
    return AggregatorType.TEMPORAL_SUM;
  }
}
