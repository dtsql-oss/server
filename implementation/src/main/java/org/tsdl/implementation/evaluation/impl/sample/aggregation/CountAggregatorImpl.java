package org.tsdl.implementation.evaluation.impl.sample.aggregation;

import java.time.Instant;
import java.util.List;
import org.tsdl.implementation.model.sample.aggregation.CountAggregator;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link CountAggregator}.
 */
public class CountAggregatorImpl extends AbstractAggregator implements CountAggregator {
  public CountAggregatorImpl(Instant lowerBound, Instant upperBound) {
    super(lowerBound, upperBound);
  }

  @Override
  protected double aggregate(List<DataPoint> input) {
    return input.size();
  }
}
