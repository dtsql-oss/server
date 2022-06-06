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
  @Override
  public double compute(List<DataPoint> dataPoints) {
    Conditions.checkNotNull(Condition.ARGUMENT, dataPoints, "Aggregator input must not be null");
    log.debug("Calculating sample (maximum) over {} data points", dataPoints.size());

    var max = dataPoints.stream()
        .mapToDouble(DataPoint::asDecimal)
        .max()
        .orElse(0.0);

    log.debug("Calculated sample (maximum) to be {}.", max);


    return max;
  }
}
