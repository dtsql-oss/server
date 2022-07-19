package org.tsdl.implementation.evaluation.impl.sample.aggregation.global;

import java.util.stream.DoubleStream;
import lombok.extern.slf4j.Slf4j;
import org.tsdl.implementation.evaluation.impl.sample.aggregation.AbstractAggregator;
import org.tsdl.implementation.model.sample.aggregation.global.GlobalMaximumAggregator;

/**
 * Default implementation of {@link GlobalMaximumAggregator}.
 */
@Slf4j
public class GlobalMaximumAggregatorImpl extends AbstractAggregator implements GlobalMaximumAggregator {
  @Override
  protected Double aggregate(DoubleStream valueStream) {
    return valueStream.max().orElse(0.0);
  }

  @Override
  protected String descriptor() {
    return "maximum";
  }
}
