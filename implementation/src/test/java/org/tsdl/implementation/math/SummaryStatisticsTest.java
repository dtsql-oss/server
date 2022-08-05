package org.tsdl.implementation.math;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.within;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.tsdl.implementation.math.impl.SummaryStatisticsImpl;

class SummaryStatisticsTest {


  @ParameterizedTest
  @MethodSource("inputData")
  void summaryStatistics_testCalculations(List<Double> data, SummaryCalculationResults results, Double tolerance, Double toleranceVariance) {
    var calculator = new SummaryStatisticsImpl();
    final var toleratedOffset = within(tolerance);
    final var toleratedOffsetVariance = within(toleranceVariance);

    calculator.ingest(() -> data);

    assertThat(calculator.populationStandardDeviation()).isEqualTo(results.populationStandardDeviation(), toleratedOffset);
    assertThat(calculator.sampleStandardDeviation()).isEqualTo(results.sampleStandardDeviation(), toleratedOffset);
    assertThat(calculator.minimum()).isEqualTo(results.minimum(), toleratedOffset);
    assertThat(calculator.maximum()).isEqualTo(results.maximum(), toleratedOffset);
    assertThat(calculator.sum()).isEqualTo(results.sum(), toleratedOffset);
    assertThat(calculator.count()).isEqualTo(results.count());
    assertThat(calculator.average()).isEqualTo(results.average(), toleratedOffset);
    assertThat(calculator.populationVariance()).isEqualTo(results.populationVariance(), toleratedOffsetVariance);
    assertThat(calculator.sampleVariance()).isEqualTo(results.sampleVariance(), toleratedOffsetVariance);
  }

  @ParameterizedTest
  @MethodSource("sumInput")
  void summaryStatistics_dedicatedSumTests(List<Double> data, double expectedSum) {
    var calculator = new SummaryStatisticsImpl();
    calculator.ingest(() -> data);
    assertThat(calculator.sum()).isEqualTo(expectedSum);
  }

  @ParameterizedTest
  @MethodSource("varianceInput")
  void summaryStatistics_dedicatedVarianceTests(List<Double> data, double expectedVariance) {
    var calculator = new SummaryStatisticsImpl();
    calculator.ingest(() -> data);
    assertThat(calculator.sampleVariance()).isEqualTo(expectedVariance);
  }

  @Test
  void summaryStatistics_testIngestion() {
    var calculator = new SummaryStatisticsImpl();

    assertThat(calculator.hasIngested()).isFalse();
    assertThat(calculator.ingest(() -> List.of(1.0, 2.0))).isTrue();
    assertThat(calculator.hasIngested()).isTrue();
    var average = calculator.average();

    assertThat(calculator.ingest(() -> List.of(3.0))).isFalse();
    assertThat(calculator.average()).isEqualTo(average);
  }

  private static Stream<Arguments> varianceInput() {
    return Stream.of(
        Arguments.of(List.of(4.0, 7.0, 13.0, 16.0), 30),
        Arguments.of(List.of(10e8 + 4, 10e8 + 7, 10e8 + 13, 10e8 + 16), 30),
        Arguments.of(List.of(10e9 + 4, 10e9 + 7, 10e9 + 13, 10e9 + 16), 30)
    );
  }

  private static Stream<Arguments> sumInput() {
    return Stream.of(
        Arguments.of(
            List.of(1.0, 10e100, 1.0, -10e100), 2
        )
    );
  }

  private static Stream<Arguments> inputData() {
    return Stream.of(
        Arguments.of(
            List.of(5.0, 23.5, 100.23, -23.0, -2434.2, 23.23, 0.02, -0.0027),
            new SummaryCalculationResults(
                811.847054,
                867.901008,
                -2434.2,
                100.23,
                -2305.2227,
                8,
                -288.152838,
                659095.639,
                753252.159
            ),
            0.00001,
            0.001
        ),
        Arguments.of(
            List.of(-12334.3, -12.23, -1222.238, -0.0075, -1238.3, 0.0, -972.0),
            new SummaryCalculationResults(
                4149.6761,
                4482.16246,
                -12334.3,
                0.0,
                -15779.0755,
                7,
                -2254.15364,
                17219811.7,
                20089780.4
            ),
            0.00001,
            0.1
        ),
        Arguments.of(
            List.of(12334.3, 12.23, 1222.238, 0.0075, 1238.3, 0.0, 972.0),
            new SummaryCalculationResults(
                4149.6761,
                4482.16246,
                0.0,
                12334.3,
                15779.0755,
                7,
                2254.15364,
                17219811.7,
                20089780.4
            ),
            0.00001,
            0.1
        ),
        Arguments.of(
            List.of(),
            new SummaryCalculationResults(
                0.0,
                0.0,
                Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY,
                0.0,
                0L,
                0.0,
                0.0,
                0.0
            ),
            0.0000001,
            0.0000001
        ),
        Arguments.of(
            List.of(0.0),
            new SummaryCalculationResults(
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                1L,
                0.0,
                0.0,
                0.0
            ),
            0.0000001,
            0.0000001
        ),
        Arguments.of(
            List.of(1.0),
            new SummaryCalculationResults(
                0.0,
                0.0,
                1.0,
                1.0,
                1.0,
                1L,
                1.0,
                0.0,
                0.0
            ),
            0.0000001,
            0.0000001
        ),
        Arguments.of(
            List.of(1.0, 2.0),
            new SummaryCalculationResults(
                0.5,
                0.707106781,
                1.0,
                2.0,
                3.0,
                2L,
                1.5,
                0.25,
                0.5
            ),
            0.0000001,
            0.0000001
        )
    );
  }

  private record SummaryCalculationResults(
      double populationStandardDeviation,
      double sampleStandardDeviation,
      double minimum,
      double maximum,
      double sum,
      long count,
      double average,
      double populationVariance,
      double sampleVariance
  ) {
  }
}
