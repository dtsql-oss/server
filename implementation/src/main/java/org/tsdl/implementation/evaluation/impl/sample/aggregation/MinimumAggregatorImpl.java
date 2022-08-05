package org.tsdl.implementation.evaluation.impl.sample.aggregation;

import java.time.Instant;
import java.util.List;
import org.tsdl.implementation.model.sample.aggregation.MinimumAggregator;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link MinimumAggregator}.
 */
public class MinimumAggregatorImpl extends AbstractSummaryAggregator implements MinimumAggregator {
  public MinimumAggregatorImpl(Instant lowerBound, Instant upperBound) {
    super(lowerBound, upperBound);
  }

  @Override
  protected double onAggregate(List<DataPoint> input) {
    return summaryStatistics.minimum();
  }
}
