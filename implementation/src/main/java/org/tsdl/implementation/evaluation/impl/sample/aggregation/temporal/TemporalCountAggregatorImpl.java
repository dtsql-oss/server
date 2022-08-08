package org.tsdl.implementation.evaluation.impl.sample.aggregation.temporal;

import java.util.List;
import org.tsdl.implementation.math.SummaryStatistics;
import org.tsdl.implementation.model.common.ParsableTsdlTimeUnit;
import org.tsdl.implementation.model.sample.aggregation.temporal.TemporalCountAggregator;
import org.tsdl.implementation.model.sample.aggregation.temporal.TimePeriod;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link TemporalCountAggregator}.
 */
public class TemporalCountAggregatorImpl extends AbstractTemporalAggregator implements TemporalCountAggregator {

  public TemporalCountAggregatorImpl(List<TimePeriod> periods, ParsableTsdlTimeUnit unit, SummaryStatistics summaryStatistics) {
    super(periods, unit, summaryStatistics);
  }

  @Override
  protected double aggregate(List<DataPoint> input) {
    return summaryStatistics.count();
  }
}
