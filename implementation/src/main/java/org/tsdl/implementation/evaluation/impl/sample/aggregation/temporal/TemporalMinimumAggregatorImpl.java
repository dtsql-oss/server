package org.tsdl.implementation.evaluation.impl.sample.aggregation.temporal;

import java.util.List;
import org.tsdl.implementation.math.SummaryStatistics;
import org.tsdl.implementation.model.common.ParsableTsdlTimeUnit;
import org.tsdl.implementation.model.sample.aggregation.temporal.TemporalMinimumAggregator;
import org.tsdl.implementation.model.sample.aggregation.temporal.TimePeriod;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link TemporalMinimumAggregator}.
 */
public class TemporalMinimumAggregatorImpl extends AbstractTemporalAggregatorWithUnit implements TemporalMinimumAggregator {

  public TemporalMinimumAggregatorImpl(List<TimePeriod> periods, ParsableTsdlTimeUnit unit, SummaryStatistics summaryStatistics) {
    super(periods, unit, summaryStatistics);
  }

  @Override
  protected double aggregate(List<DataPoint> input) {
    return convertToTargetUnit(summaryStatistics.minimum());
  }
}
