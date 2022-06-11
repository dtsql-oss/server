package org.tsdl.implementation.evaluation.impl.sample.aggregation;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.tsdl.implementation.model.sample.aggregation.CountAggregator;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link CountAggregator}.
 */
@Slf4j
public class CountAggregatorImpl implements CountAggregator {
  private Integer count;

  @Override
  public double compute(List<DataPoint> dataPoints) {
    Conditions.checkNotNull(Condition.ARGUMENT, dataPoints, "Aggregator input must not be null");
    log.debug("Calculating sample (count) over {} data points", dataPoints.size());

    count = dataPoints.size();

    log.debug("Calculated sample (count) to be {}.", count);

    return count;
  }

  @Override
  public double computedValue() {
    Conditions.checkIsTrue(Condition.STATE, this::isComputed, "Count value must have been computed before accessing it.");
    return count;
  }

  @Override
  public boolean isComputed() {
    return count != null;
  }
}
