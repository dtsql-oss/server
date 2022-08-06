package org.tsdl.implementation.math.impl;

// TODO add unit tests

import java.util.List;
import org.tsdl.implementation.math.Calculus;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.common.TsdlTimeUnit;
import org.tsdl.infrastructure.common.TsdlUtil;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link Calculus}.
 */
public class CalculusImpl implements Calculus {
  @Override
  public double definiteIntegral(List<DataPoint> dataPoints) {
    var doubleCumulativeIntegral = 0.0;

    for (var i = 1; i < dataPoints.size(); i++) {
      var currentDataPoint = dataPoints.get(i);
      var previousDataPoint = dataPoints.get(i - 1);
      var doubleArea = doubleTrapezoidalArea(previousDataPoint, currentDataPoint);
      doubleCumulativeIntegral += doubleArea;
    }

    // we were factoring out the constant factor (* 0.5) in every subterm of the sum. therefore, we apply it only once at the end of the calculation
    return doubleCumulativeIntegral * 0.5;
  }

  @Override
  public List<Double> derivative(List<DataPoint> dataPoints) {
    throw new UnsupportedOperationException("Not implemented yet.");
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
