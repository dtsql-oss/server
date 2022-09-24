package org.tsdl.implementation.math;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.tsdl.implementation.math.impl.ContinuousRegressionImpl;
import org.tsdl.implementation.math.model.LinearModel;
import org.tsdl.infrastructure.common.TsdlTimeUnit;
import org.tsdl.infrastructure.model.DataPoint;

class ContinuousRegressionTest {
  private static final ContinuousRegression CONTINUOUS_REGRESSION = new ContinuousRegressionImpl();

  @ParameterizedTest
  @MethodSource("org.tsdl.implementation.math.stub.ContinuousRegressionDataFactory#linearLeastSquaresInput")
  void linearLeastSquares(List<DataPoint> timeSeries, LinearModel expectedModel, double precision) {
    var computedModel = CONTINUOUS_REGRESSION.linearLeastSquares(timeSeries, TsdlTimeUnit.MILLISECONDS);
    final Comparator<Double> doubleComparator = (d1, d2) -> Math.abs(d1 - d2) <= precision ? 0 : 1;
    var derivative = CONTINUOUS_REGRESSION.linearLeastSquares(timeSeries, TsdlTimeUnit.MILLISECONDS);
    assertThat(derivative)
        .usingRecursiveComparison()
        .withComparatorForType(doubleComparator, Double.class)
        .isEqualTo(expectedModel);
  }
}
