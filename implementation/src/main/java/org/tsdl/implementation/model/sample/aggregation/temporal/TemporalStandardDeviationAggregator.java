package org.tsdl.implementation.model.sample.aggregation.temporal;

import org.tsdl.implementation.parsing.enums.AggregatorType;

/**
 * An aggregator calculating the population standard deviation of period durations.
 */
public interface TemporalStandardDeviationAggregator extends TemporalAggregatorWithUnit {
  @Override
  default AggregatorType type() {
    return AggregatorType.TEMPORAL_STANDARD_DEVIATION;
  }
}
