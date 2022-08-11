package org.tsdl.implementation.model.sample.aggregation.temporal;

import org.tsdl.implementation.model.common.ParsableTsdlTimeUnit;

/**
 * An aggregation operator that operates over the value dimension of a time series and has a unit argument for its result.
 */
public interface TemporalAggregatorWithUnit extends TemporalAggregator {
  ParsableTsdlTimeUnit unit();
}
