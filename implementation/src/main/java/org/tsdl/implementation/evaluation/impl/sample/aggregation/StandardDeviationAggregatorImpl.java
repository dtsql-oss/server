package org.tsdl.implementation.evaluation.impl.sample.aggregation;

import java.time.Instant;
import java.util.List;
import org.tsdl.implementation.model.sample.aggregation.StandardDeviationAggregator;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link StandardDeviationAggregator}.
 */
public class StandardDeviationAggregatorImpl extends AbstractAggregator implements StandardDeviationAggregator {
  public StandardDeviationAggregatorImpl(Instant lowerBound, Instant upperBound) {
    super(lowerBound, upperBound);
  }

  @Override
  protected double aggregate(List<DataPoint> input) {
    
    return input.stream()
        .mapToDouble(DataPoint::value)
        .average()
        .orElse(0.0);
  }
}
