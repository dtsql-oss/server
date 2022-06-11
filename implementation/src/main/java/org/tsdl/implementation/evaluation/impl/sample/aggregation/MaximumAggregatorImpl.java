package org.tsdl.implementation.evaluation.impl.sample.aggregation;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.tsdl.implementation.model.sample.aggregation.MaximumAggregator;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link MaximumAggregator}.
 */
@Slf4j
public class MaximumAggregatorImpl implements MaximumAggregator {
  private Double max;

  @Override
  public double compute(List<DataPoint> dataPoints) {
    Conditions.checkNotNull(Condition.ARGUMENT, dataPoints, "Aggregator input must not be null");
    log.debug("Calculating sample (maximum) over {} data points", dataPoints.size());

    max = dataPoints.stream()
        .mapToDouble(DataPoint::asDecimal)
        .max()
        .orElse(0.0);

    log.debug("Calculated sample (maximum) to be {}.", max);

    return max;
  }

  @Override
  public double value() {
    Conditions.checkIsTrue(Condition.STATE, this::isComputed, "Maximum value must have been computed before accessing it.");
    return max;
  }

  @Override
  public boolean isComputed() {
    return max != null;
  }
}
