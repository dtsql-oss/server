package org.tsdl.implementation.evaluation.impl.sample.aggregation.global;

import java.util.stream.DoubleStream;
import lombok.extern.slf4j.Slf4j;
import org.tsdl.implementation.evaluation.impl.sample.aggregation.AbstractAggregator;
import org.tsdl.implementation.model.sample.aggregation.global.GlobalMinimumAggregator;

/**
 * Default implementation of {@link GlobalMinimumAggregator}.
 */
@Slf4j
public class GlobalMinimumAggregatorImpl extends AbstractAggregator implements GlobalMinimumAggregator {
  @Override
  protected Double aggregate(DoubleStream valueStream) {
    return valueStream.min().orElse(0.0);
  }

  @Override
  protected String descriptor() {
    return "global sample (count)";
  }
}
