package org.tsdl.implementation.evaluation.impl.sample.aggregation.global;

import java.util.stream.DoubleStream;
import lombok.extern.slf4j.Slf4j;
import org.tsdl.implementation.evaluation.impl.sample.aggregation.AbstractAggregator;
import org.tsdl.implementation.model.sample.aggregation.global.GlobalCountAggregator;

/**
 * Default implementation of {@link GlobalCountAggregator}.
 */
@Slf4j
public class GlobalCountAggregatorImpl extends AbstractAggregator implements GlobalCountAggregator {
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
