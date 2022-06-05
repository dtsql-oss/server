package org.tsdl.implementation.evaluation.impl.sample.aggregation;

import java.util.List;
import org.tsdl.implementation.model.sample.aggregation.SumAggregator;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link SumAggregator}.
 */
public class SumAggregatorImpl implements SumAggregator {
  @Override
  public double compute(List<DataPoint> dataPoints) {
    throw new UnsupportedOperationException();
  }
}
