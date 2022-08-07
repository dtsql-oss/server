package org.tsdl.implementation.evaluation.impl.sample.aggregation.temporal;

import java.util.List;
import org.tsdl.implementation.model.common.ParsableTsdlTimeUnit;
import org.tsdl.implementation.model.sample.aggregation.temporal.TemporalSumAggregator;
import org.tsdl.implementation.model.sample.aggregation.temporal.TimePeriod;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link TemporalSumAggregator}.
 */
public record TemporalSumAggregatorImpl(List<TimePeriod> periods, ParsableTsdlTimeUnit unit) implements TemporalSumAggregator {
  public TemporalSumAggregatorImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, periods, "Periods must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, unit, "Unit must not be null.");
  }

  @Override
  public double compute(String sampleIdentifier, List<DataPoint> dataPoints) {
    return 0;
  }

  @Override
  public double value() {
    return 0;
  }

  @Override
  public boolean isComputed() {
    return false;
  }
}
