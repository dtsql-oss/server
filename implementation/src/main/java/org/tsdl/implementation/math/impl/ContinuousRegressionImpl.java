package org.tsdl.implementation.math.impl;

import java.util.ArrayList;
import java.util.List;
import org.tsdl.implementation.factory.TsdlComponentFactory;
import org.tsdl.implementation.math.ContinuousRegression;
import org.tsdl.implementation.math.model.LinearModel;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.common.TsdlTimeUnit;
import org.tsdl.infrastructure.common.TsdlUtil;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link ContinuousRegression}.
 */
public class ContinuousRegressionImpl implements ContinuousRegression {
  @Override
  public LinearModel linearLeastSquares(List<DataPoint> dataPoints) {
    Conditions.checkIsGreaterThanOrEqual(Condition.ARGUMENT, dataPoints.size(), 2, "The time series must exhibit at least two data points.");
    var zeroDate = dataPoints.get(0).timestamp();
    var x = new ArrayList<Double>(dataPoints.size());
    var y = new ArrayList<Double>(dataPoints.size());
    for (var dataPoint : dataPoints) {
      x.add(TsdlUtil.getTimespan(zeroDate, dataPoint.timestamp(), TsdlTimeUnit.MILLISECONDS));
      y.add(dataPoint.value());
    }

    var xSummary = TsdlComponentFactory.INSTANCE.summaryStatistics();
    var ySummary = TsdlComponentFactory.INSTANCE.summaryStatistics();
    xSummary.ingest(() -> x);
    ySummary.ingest(() -> y);

    var xTimesYSummary = TsdlComponentFactory.INSTANCE.summaryStatistics();
    var xTimesY = new ArrayList<Double>();
    for (int i = 0; i < x.size(); i++) {
      xTimesY.add(x.get(i) * y.get(i));
    }
    xTimesYSummary.ingest(() -> xTimesY);

    var xSquaredSummary = TsdlComponentFactory.INSTANCE.summaryStatistics();
    xSquaredSummary.ingest(() -> x.stream().map(v -> v * v).toList());

    var count = dataPoints.size();
    var sumX = xSummary.sum();
    var sumY = ySummary.sum();
    var avgX = xSummary.average();
    var avgY = ySummary.average();
    var sumXTimesY = xTimesY.stream().mapToDouble(v -> v).sum();
    var sumXSquared = x.stream().mapToDouble(v -> v * v).sum();

    var inverseCount = 1.0 / count;
    var beta1 = (sumXTimesY - (inverseCount * sumX * sumY)) / (sumXSquared - (inverseCount * sumX * sumX));
    var beta0 = avgY - avgX * beta1;

    return LinearModel.of(beta1, beta0);
  }
}
