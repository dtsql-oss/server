package org.tsdl.implementation.evaluation.impl.sample.aggregation.local;

import java.time.Instant;
import java.util.stream.DoubleStream;
import lombok.extern.slf4j.Slf4j;
import org.tsdl.implementation.evaluation.impl.sample.aggregation.AbstractAggregator;
import org.tsdl.implementation.model.sample.aggregation.local.LocalCountAggregator;

/**
 * Default implementation of {@link LocalCountAggregator}.
 */
@Slf4j
public class LocalCountAggregatorImpl extends AbstractAggregator implements LocalCountAggregator {
  private final Instant lowerBound;
  private final Instant upperBound;

  public LocalCountAggregatorImpl(Instant lowerBound, Instant upperBound) {
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
    var count = valueStream.count();
    return Long.valueOf(count).doubleValue();
  }

  @Override
  protected String descriptor() {
    return "count";
  }
}
