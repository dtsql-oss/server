package org.tsdl.implementation.evaluation.impl.sample.aggregation;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.tsdl.implementation.model.sample.aggregation.MinimumAggregator;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link MinimumAggregator}.
 */
@Slf4j
public class MinimumAggregatorImpl implements MinimumAggregator {
  @Override
  public double compute(List<DataPoint> dataPoints) {
    Conditions.checkNotNull(Condition.ARGUMENT, dataPoints, "Aggregator input must not be null");
    log.debug("Calculating sample (minimum) over {} data points", dataPoints.size());

    var min = dataPoints.stream()
        .mapToDouble(DataPoint::asDecimal)
        .min()
        .orElse(0.0);

    log.debug("Calculated sample (minimum) to be {}.", min);

    return min;
  }
}
