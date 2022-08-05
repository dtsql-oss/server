package org.tsdl.implementation.evaluation.impl.sample.aggregation;

import java.time.Instant;
import java.util.List;
import org.tsdl.implementation.model.sample.aggregation.SumAggregator;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link SumAggregator}.
 */
public class SumAggregatorImpl extends AbstractSummaryAggregator implements SumAggregator {
  public SumAggregatorImpl(Instant lowerBound, Instant upperBound) {
    super(lowerBound, upperBound);
  }

  @Override
  protected double onAggregate(List<DataPoint> input) {
    return summaryStatistics.sum();
  }
}
