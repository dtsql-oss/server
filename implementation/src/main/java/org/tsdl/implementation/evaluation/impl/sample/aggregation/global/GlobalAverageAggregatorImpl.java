package org.tsdl.implementation.evaluation.impl.sample.aggregation.global;

import java.util.stream.DoubleStream;
import lombok.extern.slf4j.Slf4j;
import org.tsdl.implementation.evaluation.impl.sample.aggregation.AbstractAggregator;
import org.tsdl.implementation.model.sample.aggregation.global.GlobalAverageAggregator;

/**
 * Default implementation of {@link GlobalAverageAggregator}.
 */
@Slf4j
public class GlobalAverageAggregatorImpl extends AbstractAggregator implements GlobalAverageAggregator {
  @Override
  protected Double aggregate(DoubleStream valueStream) {
    return valueStream.average().orElse(0.0);
  }

  @Override
  protected String descriptor() {
    return "global sample (arithmetic mean)";
  }
}
