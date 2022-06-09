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
  @Override
  public double compute(List<DataPoint> dataPoints) {
    Conditions.checkNotNull(Condition.ARGUMENT, dataPoints, "Aggregator input must not be null");
    log.debug("Calculating sample (count) over {} data points", dataPoints.size());

    var count = dataPoints.size();

    log.debug("Calculated sample (count) to be {}.", count);

    return count;
  }
}
