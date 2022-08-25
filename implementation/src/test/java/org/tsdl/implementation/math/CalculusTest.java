package org.tsdl.implementation.math;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.tsdl.implementation.math.impl.CalculusImpl;
import org.tsdl.infrastructure.common.TsdlTimeUnit;
import org.tsdl.infrastructure.model.DataPoint;

class CalculusTest {
  private static final Calculus CALCULUS = new CalculusImpl();

  @ParameterizedTest
  @MethodSource("org.tsdl.implementation.math.stub.CalculusDataFactory#derivativeInputs")
  void derivative(List<DataPoint> input, TsdlTimeUnit unit, List<DataPoint> expectedDerivative, double precision) {
    // tes data has 9 exact decimal places, therefore precision 1E-10
    final Comparator<Double> doubleComparator = (d1, d2) -> Math.abs(d1 - d2) <= precision ? 0 : 1;
    var derivative = CALCULUS.derivative(input, unit);
    assertThat(derivative)
        .usingRecursiveComparison()
        .withComparatorForType(doubleComparator, Double.class)
        .isEqualTo(expectedDerivative);
  }
}
