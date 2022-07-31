package org.tsdl.implementation.evaluation.impl.sample.aggregation;

import java.time.Instant;
import java.util.List;
import org.tsdl.implementation.model.sample.aggregation.MaximumAggregator;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link MaximumAggregator}.
 */
public class MaximumAggregatorImpl extends AbstractAggregator implements MaximumAggregator {
  public MaximumAggregatorImpl(Instant lowerBound, Instant upperBound) {
    super(lowerBound, upperBound);
  }

  @Override
  protected double aggregate(List<DataPoint> input) {
    return input.stream()
        .mapToDouble(DataPoint::value)
        .max()
        .orElse(0.0);
  }
}
