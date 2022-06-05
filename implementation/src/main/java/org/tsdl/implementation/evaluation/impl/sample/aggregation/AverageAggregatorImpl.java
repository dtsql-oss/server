package org.tsdl.implementation.evaluation.impl.sample.aggregation;

import java.util.List;
import org.tsdl.implementation.model.sample.aggregation.AverageAggregator;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link AverageAggregator}.
 */
public class AverageAggregatorImpl implements AverageAggregator {
  @Override
  public double compute(List<DataPoint> dataPoints) {
    throw new UnsupportedOperationException();
  }
}
