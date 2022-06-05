package org.tsdl.implementation.evaluation.impl.sample.aggregation;

import java.util.List;
import org.tsdl.implementation.model.sample.aggregation.MinimumAggregator;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link MinimumAggregator}.
 */
public class MinimumAggregatorImpl implements MinimumAggregator {
  @Override
  public double compute(List<DataPoint> dataPoints) {
    throw new UnsupportedOperationException();
  }
}
