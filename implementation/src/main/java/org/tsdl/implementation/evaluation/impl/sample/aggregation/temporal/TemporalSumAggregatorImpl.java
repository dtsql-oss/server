package org.tsdl.implementation.evaluation.impl.sample.aggregation.temporal;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.tsdl.implementation.math.SummaryStatistics;
import org.tsdl.implementation.model.common.ParsableTsdlTimeUnit;
import org.tsdl.implementation.model.sample.aggregation.temporal.TemporalSumAggregator;
import org.tsdl.implementation.model.sample.aggregation.temporal.TimePeriod;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link TemporalSumAggregator}.
 */
@EqualsAndHashCode(callSuper = true)
@ToString
public class TemporalSumAggregatorImpl extends AbstractTemporalAggregator implements TemporalSumAggregator {

  public TemporalSumAggregatorImpl(List<TimePeriod> periods, ParsableTsdlTimeUnit unit, SummaryStatistics summaryStatistics) {
    super(periods, unit, summaryStatistics);
  }

  @Override
  protected double aggregate(List<DataPoint> input) {
    return convertToTargetUnit(summaryStatistics.sum());
  }
}
