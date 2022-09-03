package org.tsdl.implementation.evaluation.impl.event.strategy;

import java.util.List;
import org.tsdl.implementation.model.choice.AnnotatedTsdlPeriod;
import org.tsdl.implementation.model.event.TsdlEvent;
import org.tsdl.implementation.model.event.strategy.DecreaseEventStrategy;
import org.tsdl.infrastructure.model.DataPoint;

public class DecreaseEventStrategyImpl implements DecreaseEventStrategy {
  @Override
  public List<AnnotatedTsdlPeriod> detectPeriods(List<DataPoint> dataPoints, List<TsdlEvent> events) {
    throw new UnsupportedOperationException("decrease");
  }
}