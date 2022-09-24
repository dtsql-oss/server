package org.tsdl.implementation.evaluation.impl.sample.aggregation.value;

import java.time.Instant;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.tsdl.implementation.math.SummaryStatistics;
import org.tsdl.implementation.model.sample.aggregation.value.MaximumAggregator;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link MaximumAggregator}.
 */
@EqualsAndHashCode(callSuper = true)
@ToString
public class MaximumAggregatorImpl extends AbstractSummaryAggregator implements MaximumAggregator {
  public MaximumAggregatorImpl(Instant lowerBound, Instant upperBound, SummaryStatistics summaryStatistics) {
    super(lowerBound, upperBound, summaryStatistics);
  }

  @Override
  protected double onAggregate(List<DataPoint> input) {
    return summaryStatistics.maximum();
  }
}
