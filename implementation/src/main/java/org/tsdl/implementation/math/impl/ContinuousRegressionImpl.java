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
  public LinearModel linearLeastSquares(List<DataPoint> dataPoints, TsdlTimeUnit timeResolution) {
    Conditions.checkIsGreaterThanOrEqual(Condition.ARGUMENT, dataPoints.size(), 2, "The time series must exhibit at least two data points.");
    var zeroDate = dataPoints.get(0).timestamp();

    var x = new ArrayList<Double>(dataPoints.size());
    var y = new ArrayList<Double>(dataPoints.size());
    for (var dataPoint : dataPoints) {
      x.add(TsdlUtil.getTimespan(zeroDate, dataPoint.timestamp(), timeResolution));
      y.add(dataPoint.value());
    }

    var summaryX = TsdlComponentFactory.INSTANCE.summaryStatistics();
    var summaryY = TsdlComponentFactory.INSTANCE.summaryStatistics();
    summaryX.ingest(() -> x);
    summaryY.ingest(() -> y);

    var summaryXtimesY = TsdlComponentFactory.INSTANCE.summaryStatistics();
    var multiplyXy = new ArrayList<Double>();
    for (var i = 0; i < x.size(); i++) {
      multiplyXy.add(x.get(i) * y.get(i));
    }
    summaryXtimesY.ingest(() -> multiplyXy);

    var summaryXsquared = TsdlComponentFactory.INSTANCE.summaryStatistics();
    summaryXsquared.ingest(() -> x.stream().map(v -> v * v).toList());

    var count = dataPoints.size();
    var sumX = summaryX.sum();
    var sumY = summaryY.sum();
    var avgX = summaryX.average();
    var avgY = summaryY.average();
    var sumXtimesY = multiplyXy.stream().mapToDouble(v -> v).sum();
    var sumXsquared = x.stream().mapToDouble(v -> v * v).sum();

    var inverseCount = 1.0 / count;
    var beta1 = (sumXtimesY - (inverseCount * sumX * sumY)) / (sumXsquared - (inverseCount * sumX * sumX));
    var beta0 = avgY - avgX * beta1;

    return LinearModel.of(beta1, beta0);
  }
}
