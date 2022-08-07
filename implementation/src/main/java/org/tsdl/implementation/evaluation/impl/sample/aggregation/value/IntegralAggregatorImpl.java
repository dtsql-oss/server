package org.tsdl.implementation.evaluation.impl.sample.aggregation.value;

import java.time.Instant;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.tsdl.implementation.math.Calculus;
import org.tsdl.implementation.model.sample.aggregation.value.IntegralAggregator;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link IntegralAggregator}.
 */
@EqualsAndHashCode(callSuper = true)
@ToString
public class IntegralAggregatorImpl extends AbstractValueAggregator implements IntegralAggregator {
  private final Calculus calculus;

  public IntegralAggregatorImpl(Instant lowerBound, Instant upperBound, Calculus calculus) {
    super(lowerBound, upperBound);
    Conditions.checkNotNull(Condition.ARGUMENT, calculus, "Calculus instance must not be null.");
    this.calculus = calculus;
  }

  @Override
  protected double aggregate(List<DataPoint> input) {
    return calculus.definiteIntegral(input);
  }
}
