package org.tsdl.implementation.evaluation.impl.sample.aggregation.local;

import java.time.Instant;
import java.util.stream.DoubleStream;
import lombok.extern.slf4j.Slf4j;
import org.tsdl.implementation.evaluation.impl.sample.aggregation.AbstractAggregator;
import org.tsdl.implementation.model.sample.aggregation.local.LocalAverageAggregator;

/**
 * Default implementation of {@link LocalAverageAggregator}.
 */
@Slf4j
public class LocalAverageAggregatorImpl extends AbstractAggregator implements LocalAverageAggregator {
  private final Instant lowerBound;
  private final Instant upperBound;

  public LocalAverageAggregatorImpl(Instant lowerBound, Instant upperBound) {
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
    return valueStream.average().orElse(0.0);
  }

  @Override
  protected String descriptor() {
    return "local sample (arithmetic mean) from '%s' until '%s'".formatted(lowerBound, upperBound);
  }
}
