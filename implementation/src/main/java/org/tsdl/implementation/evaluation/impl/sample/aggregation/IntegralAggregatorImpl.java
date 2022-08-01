package org.tsdl.implementation.evaluation.impl.sample.aggregation;

import java.time.Instant;
import java.util.List;
import org.tsdl.implementation.model.sample.aggregation.IntegralAggregator;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.common.TsdlTimeUnit;
import org.tsdl.infrastructure.common.TsdlUtil;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link IntegralAggregator}.
 */
public class IntegralAggregatorImpl extends AbstractAggregator implements IntegralAggregator {
  public IntegralAggregatorImpl(Instant lowerBound, Instant upperBound) {
    super(lowerBound, upperBound);
  }

  @Override
  protected double aggregate(List<DataPoint> input) {
    var doubleCumulativeIntegral = 0.0;

    for (var i = 1; i < input.size(); i++) {
      var currentDataPoint = input.get(i);
      var previousDataPoint = input.get(i - 1);
      var doubleArea = doubleTrapezoidalArea(previousDataPoint, currentDataPoint);
      doubleCumulativeIntegral += doubleArea;
    }

    // we were extracting the constant factor (* 0.5) in every subterm of the sum and apply it only in the end
    return doubleCumulativeIntegral * 0.5;

  }

  private double doubleTrapezoidalArea(DataPoint dp1, DataPoint dp2) {
    var a = dp1.value();
    var c = dp2.value();
    var h = TsdlUtil.getTimespan(dp1.timestamp(), dp2.timestamp(), TsdlTimeUnit.SECONDS);
    Conditions.checkIsTrue(
        Condition.STATE,
        h >= 0,
        "Trapezoid height for data points at %s and %s is negative. Are the data points not in ascending order (by date)?",
        dp1.timestamp(),
        dp2.timestamp()
    );
    return (a + c) * h;
  }
}
