package org.tsdl.implementation.evaluation.impl.sample.aggregation.temporal;

import java.util.List;
import org.tsdl.implementation.math.SummaryStatistics;
import org.tsdl.implementation.model.common.ParsableTsdlTimeUnit;
import org.tsdl.implementation.model.sample.aggregation.temporal.TemporalMaximumAggregator;
import org.tsdl.implementation.model.sample.aggregation.temporal.TimePeriod;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link TemporalMaximumAggregator}.
 */
public class TemporalMaximumAggregatorImpl extends AbstractTemporalAggregator implements TemporalMaximumAggregator {

  public TemporalMaximumAggregatorImpl(List<TimePeriod> periods, ParsableTsdlTimeUnit unit, SummaryStatistics summaryStatistics) {
    super(periods, unit, summaryStatistics);
  }

  @Override
  protected double aggregate(List<DataPoint> input) {
    return convertToTargetUnit(summaryStatistics.maximum());
  }
}
