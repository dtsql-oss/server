package org.tsdl.implementation.evaluation.impl.sample.aggregation;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.tsdl.implementation.model.sample.aggregation.SumAggregator;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link SumAggregator}.
 */
@Slf4j
public class SumAggregatorImpl implements SumAggregator {
  @Override
  public double compute(List<DataPoint> dataPoints) {
    Conditions.checkNotNull(Condition.ARGUMENT, dataPoints, "Aggregator input must not be null");
    log.debug("Calculating sample (sum) over {} data points", dataPoints.size());

    var sum = dataPoints.stream()
        .mapToDouble(DataPoint::asDecimal)
        .sum();

    log.debug("Calculated sample (sum) to be {}.", sum);

    return sum;
  }
}
