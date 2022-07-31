package org.tsdl.implementation.evaluation.impl.sample.aggregation;

import java.time.Instant;
import java.util.List;
import org.tsdl.implementation.model.sample.aggregation.MinimumAggregator;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link MinimumAggregator}.
 */
public class MinimumAggregatorImpl extends AbstractAggregator implements MinimumAggregator {
  public MinimumAggregatorImpl(Instant lowerBound, Instant upperBound) {
    super(lowerBound, upperBound);
  }

  @Override
  protected double aggregate(List<DataPoint> input) {
    return input.stream()
        .mapToDouble(DataPoint::value)
        .min()
        .orElse(0.0);
  }
}
