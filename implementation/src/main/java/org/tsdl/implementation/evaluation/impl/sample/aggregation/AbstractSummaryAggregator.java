package org.tsdl.implementation.evaluation.impl.sample.aggregation;

import java.time.Instant;
import java.util.List;
import org.tsdl.implementation.math.SummaryStatistics;
import org.tsdl.implementation.model.sample.aggregation.TsdlAggregator;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;

abstract class AbstractSummaryAggregator extends AbstractAggregator implements TsdlAggregator {
  protected SummaryStatistics summaryStatistics;

  protected abstract double onAggregate(List<DataPoint> input);

  /**
   * Initializes a {@link AbstractAggregator} instance.
   */
  public AbstractSummaryAggregator(Instant lowerBound, Instant upperBound, SummaryStatistics summaryStatistics) {
    super(lowerBound, upperBound);
    Conditions.checkNotNull(Condition.ARGUMENT, summaryStatistics, "Summary statistics calculator must not be null.");
    this.summaryStatistics = summaryStatistics;
  }

  @Override
  protected double aggregate(List<DataPoint> input) {
    Conditions.checkNotNull(Condition.STATE, summaryStatistics, "Summary statistics calculator must be set before sample calculation.");
    summaryStatistics.ingest(() -> input.stream().map(DataPoint::value).toList());
    return onAggregate(input);
  }
}
