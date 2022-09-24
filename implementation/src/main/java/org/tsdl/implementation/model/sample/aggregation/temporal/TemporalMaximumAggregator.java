package org.tsdl.implementation.model.sample.aggregation.temporal;

import org.tsdl.implementation.parsing.enums.AggregatorType;

/**
 * An aggregator calculating the maximum of period durations.
 */
public interface TemporalMaximumAggregator extends TemporalAggregatorWithUnit {
  @Override
  default AggregatorType type() {
    return AggregatorType.TEMPORAL_MAXIMUM;
  }
}
