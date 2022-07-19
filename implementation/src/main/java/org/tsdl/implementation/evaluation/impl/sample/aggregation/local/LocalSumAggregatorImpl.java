package org.tsdl.implementation.evaluation.impl.sample.aggregation.local;

import java.time.Instant;
import java.util.stream.DoubleStream;
import lombok.extern.slf4j.Slf4j;
import org.tsdl.implementation.evaluation.impl.sample.aggregation.AbstractAggregator;
import org.tsdl.implementation.model.sample.aggregation.local.LocalSumAggregator;

/**
 * Default implementation of {@link LocalSumAggregator}.
 */
@Slf4j
public class LocalSumAggregatorImpl extends AbstractAggregator implements LocalSumAggregator {
  private final Instant lowerBound;
  private final Instant upperBound;

  public LocalSumAggregatorImpl(Instant lowerBound, Instant upperBound) {
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
  }

  @Override
  public Instant lowerBound() {
    return lowerBound;
  }

  @Override
  public Instant upperBound() {
    return upperBound;
  }

  @Override
  protected Double aggregate(DoubleStream valueStream) {
    return valueStream.sum();
  }

  @Override
  protected String descriptor() {
    return "sum";
  }
}
