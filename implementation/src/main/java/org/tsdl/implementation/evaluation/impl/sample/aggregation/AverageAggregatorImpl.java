package org.tsdl.implementation.evaluation.impl.sample.aggregation;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.tsdl.implementation.model.sample.aggregation.AverageAggregator;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link AverageAggregator}.
 */
@Slf4j
public class AverageAggregatorImpl implements AverageAggregator {
  private Double avg;

  @Override
  public double compute(List<DataPoint> dataPoints) {
    Conditions.checkNotNull(Condition.ARGUMENT, dataPoints, "Aggregator input must not be null.");
    log.debug("Calculating sample (arithmetic mean) over {} data points.", dataPoints.size());

    avg = dataPoints.stream()
        .mapToDouble(DataPoint::asDecimal)
        .average()
        .orElse(0.0);

    log.debug("Calculated sample (arithmetic mean) to be {}.", avg);

    return avg;
  }

  @Override
  public double value() {
    Conditions.checkIsTrue(Condition.STATE, this::isComputed, "Average value must have been computed before accessing it.");
    return avg;
  }

  @Override
  public boolean isComputed() {
    return avg != null;
  }
}
