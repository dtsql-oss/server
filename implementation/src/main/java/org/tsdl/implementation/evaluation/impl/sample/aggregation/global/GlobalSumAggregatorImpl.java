package org.tsdl.implementation.evaluation.impl.sample.aggregation.global;

import java.util.stream.DoubleStream;
import lombok.extern.slf4j.Slf4j;
import org.tsdl.implementation.evaluation.impl.sample.aggregation.AbstractAggregator;
import org.tsdl.implementation.model.sample.aggregation.global.GlobalSumAggregator;

/**
 * Default implementation of {@link GlobalSumAggregator}.
 */
@Slf4j
public class GlobalSumAggregatorImpl extends AbstractAggregator implements GlobalSumAggregator {
  @Override
  protected Double aggregate(DoubleStream valueStream) {
    return valueStream.sum();
  }

  @Override
  protected String descriptor() {
    return "global sample (count)";
  }
}
