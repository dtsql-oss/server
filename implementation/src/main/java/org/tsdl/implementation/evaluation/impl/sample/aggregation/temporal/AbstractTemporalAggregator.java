package org.tsdl.implementation.evaluation.impl.sample.aggregation.temporal;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.tsdl.implementation.math.SummaryStatistics;
import org.tsdl.implementation.model.sample.aggregation.temporal.TemporalAggregator;
import org.tsdl.implementation.model.sample.aggregation.temporal.TimePeriod;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.common.TsdlTimeUnit;
import org.tsdl.infrastructure.model.DataPoint;

@Slf4j
abstract class AbstractTemporalAggregator implements TemporalAggregator {
  protected final List<TimePeriod> periods;
  protected final SummaryStatistics summaryStatistics;
  private double sampleValue = Double.NaN;
  private final String descriptor;

  public AbstractTemporalAggregator(List<TimePeriod> periods, SummaryStatistics summaryStatistics) {
    this.periods = Conditions.checkNotNull(Condition.ARGUMENT, periods, "Periods must not be null.");
    this.summaryStatistics = Conditions.checkNotNull(Condition.ARGUMENT, summaryStatistics, "Summary statistics calculator must not be null.");
    this.descriptor = "%s over %s periods".formatted(type(), periods.size());
  }

  protected abstract double aggregate(List<DataPoint> input);

  @Override
  public double compute(String sampleIdentifier, List<DataPoint> dataPoints) {
    Conditions.checkNotNull(Condition.ARGUMENT, dataPoints, "Aggregator input must not be null");
    log.info("Calculating sample '{}' ({}).", sampleIdentifier, descriptor);

    summaryStatistics.ingest(() -> periods.stream().map(period -> period.duration(TsdlTimeUnit.MILLISECONDS)).toList());
    sampleValue = aggregate(dataPoints);
    Conditions.checkNotNull(Condition.STATE, sampleValue, "Sample computation failed, aggregate value must not be null.");

    log.info("Calculated sample '{}' ({}) to be {}.", sampleIdentifier, descriptor, sampleValue);

    return sampleValue;
  }

  @Override
  public List<TimePeriod> periods() {
    return periods;
  }

  @Override
  public double value() {
    Conditions.checkIsTrue(Condition.STATE, this::isComputed, "Value of %s must have been computed before trying to access it.", descriptor);
    return sampleValue;
  }

  @Override
  public boolean isComputed() {
    return !Double.isNaN(sampleValue);
  }
}
