package org.tsdl.implementation.model.sample.aggregation.temporal;

import java.util.List;
import org.tsdl.implementation.model.common.ParsableTsdlTimeUnit;
import org.tsdl.implementation.model.sample.aggregation.TsdlAggregator;

/**
 * An aggregation operator that operates over the value dimension of a time series.
 */
public interface TemporalAggregator extends TsdlAggregator {
  List<TimePeriod> periods();

  ParsableTsdlTimeUnit unit();
}
