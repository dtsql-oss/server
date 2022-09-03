package org.tsdl.implementation.evaluation.impl.event.strategy;

import java.util.List;
import org.tsdl.implementation.model.choice.AnnotatedTsdlPeriod;
import org.tsdl.implementation.model.event.TsdlEvent;
import org.tsdl.implementation.model.event.strategy.ConstantEventStrategy;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link ConstantEventStrategy}.
 */
public class ConstantEventStrategyImpl implements ConstantEventStrategy {
  @Override
  public List<AnnotatedTsdlPeriod> detectPeriods(List<DataPoint> dataPoints, List<TsdlEvent> events) {
    throw new UnsupportedOperationException("constant");
  }
}
