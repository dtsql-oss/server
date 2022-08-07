package org.tsdl.implementation.evaluation.impl.sample.aggregation;

import java.time.Instant;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.tsdl.implementation.math.SummaryStatistics;
import org.tsdl.implementation.model.sample.aggregation.MinimumAggregator;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link MinimumAggregator}.
 */
@EqualsAndHashCode(callSuper = true)
@ToString
public class MinimumAggregatorImpl extends AbstractSummaryAggregator implements MinimumAggregator {
  public MinimumAggregatorImpl(Instant lowerBound, Instant upperBound, SummaryStatistics summaryStatistics) {
    super(lowerBound, upperBound, summaryStatistics);
  }

  @Override
  protected double onAggregate(List<DataPoint> input) {
    return summaryStatistics.minimum();
  }
}
