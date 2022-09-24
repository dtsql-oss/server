package org.tsdl.implementation.evaluation.impl.sample.aggregation.temporal;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.tsdl.implementation.math.SummaryStatistics;
import org.tsdl.implementation.model.common.ParsableTsdlTimeUnit;
import org.tsdl.implementation.model.sample.aggregation.temporal.TemporalAggregatorWithUnit;
import org.tsdl.implementation.model.sample.aggregation.temporal.TimePeriod;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.common.TsdlTimeUnit;
import org.tsdl.infrastructure.common.TsdlUtil;
import org.tsdl.infrastructure.model.DataPoint;

@Slf4j
abstract class AbstractTemporalAggregatorWithUnit extends AbstractTemporalAggregator implements TemporalAggregatorWithUnit {
  protected final ParsableTsdlTimeUnit unit;

  protected AbstractTemporalAggregatorWithUnit(List<TimePeriod> periods, ParsableTsdlTimeUnit unit, SummaryStatistics summaryStatistics) {
    super(periods, summaryStatistics);
    this.unit = Conditions.checkNotNull(Condition.ARGUMENT, unit, "Unit must not be null.");
  }

  protected abstract double aggregate(List<DataPoint> input);

  protected double convertToTargetUnit(double millis) {
    return TsdlUtil.convertUnit(millis, TsdlTimeUnit.MILLISECONDS, unit.modelEquivalent());
  }

  @Override
  public ParsableTsdlTimeUnit unit() {
    return unit;
  }
}
