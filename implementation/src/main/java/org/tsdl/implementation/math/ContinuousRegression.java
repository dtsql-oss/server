package org.tsdl.implementation.math;

import java.util.List;
import org.tsdl.implementation.math.model.LinearModel;
import org.tsdl.infrastructure.common.TsdlTimeUnit;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Provides methods executing a regression analysis to compute continuous models from a set of discrete data points.
 */
public interface ContinuousRegression {
  LinearModel linearLeastSquares(List<DataPoint> dataPoints, TsdlTimeUnit timeResolution);
}
