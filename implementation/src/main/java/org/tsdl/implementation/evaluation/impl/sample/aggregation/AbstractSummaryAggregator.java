package org.tsdl.implementation.evaluation.impl.sample.aggregation;

import java.time.Instant;
import java.util.List;
import org.tsdl.implementation.math.SummaryStatistics;
import org.tsdl.implementation.model.sample.aggregation.SummaryAggregator;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;

abstract class AbstractSummaryAggregator extends AbstractAggregator implements SummaryAggregator {
  protected SummaryStatistics summaryStatistics;

  protected abstract double onAggregate(List<DataPoint> input);

  /**
   * Initializes a {@link AbstractAggregator} instance.
   */
  public AbstractSummaryAggregator(Instant lowerBound, Instant upperBound) {
    super(lowerBound, upperBound);
  }

  @Override
  protected double aggregate(List<DataPoint> input) {
    Conditions.checkNotNull(Condition.STATE, summaryStatistics, "Summary statistics calculator must be set before sample calculation.");
    summaryStatistics.ingest(() -> input.stream().map(DataPoint::value).toList());
    return onAggregate(input);
  }

  @Override
  public void setStatistics(SummaryStatistics statistics) {
    this.summaryStatistics = statistics;
  }
}
