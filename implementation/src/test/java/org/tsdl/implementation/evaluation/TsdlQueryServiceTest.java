package org.tsdl.implementation.evaluation;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.tsdl.infrastructure.api.QueryService;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.infrastructure.model.MultipleScalarResult;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.infrastructure.model.QueryResultType;
import org.tsdl.infrastructure.model.TsdlDataPoints;
import org.tsdl.infrastructure.model.TsdlLogEvent;
import org.tsdl.infrastructure.model.TsdlPeriod;
import org.tsdl.infrastructure.model.TsdlPeriodSet;
import org.tsdl.infrastructure.model.impl.SingularScalarResultImpl;
import org.tsdl.testutil.creation.provider.TsdlTestSource;
import org.tsdl.testutil.creation.provider.TsdlTestSources;
import org.tsdl.testutil.visualization.api.TsdlTestVisualization;
import org.tsdl.testutil.visualization.impl.TsdlTestVisualizer;

@ExtendWith(TsdlTestVisualizer.class)
class TsdlQueryServiceTest {
  private static final String DATA_ROOT = "data/query/";
  private static final QueryService queryService = new TsdlQueryService();

  @Nested
  @DisplayName("sample tests")
  class QuerySample {
    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.QueryServiceDataFactory#globalAggregates")
    void querySample_yieldGlobalSample(List<DataPoint> dps, String sampleDefinition, Double expectedResult) {
      var query = "WITH SAMPLES: %s AS s1  YIELD: sample s1".formatted(sampleDefinition);

      var result = queryService.query(dps, query);

      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(SingularScalarResultImpl.class))
          .extracting(SingularScalarResultImpl::value, InstanceOfAssertFactories.DOUBLE)
          .isEqualTo(expectedResult);
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.QueryServiceDataFactory#globalAggregatesSet")
    void querySample_yieldGlobalSampleSet(List<DataPoint> dps, String sampleDefinition, String yieldComponent, Double[] expectedResults) {
      var query = """
          WITH SAMPLES:
                      %s
                    YIELD:
                      samples %s""".formatted(sampleDefinition, yieldComponent);

      var result = queryService.query(dps, query);

      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(MultipleScalarResult.class))
          .extracting(MultipleScalarResult::values, InstanceOfAssertFactories.list(Double.class))
          .containsExactly(expectedResults);
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.QueryServiceDataFactory#localAggregates")
    void querySample_yieldLocalSample(List<DataPoint> dps, String sampleDefinition, Double expectedResult) {
      var query = "WITH SAMPLES: %s AS s1  YIELD: sample s1".formatted(sampleDefinition);

      var result = queryService.query(dps, query);

      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(SingularScalarResultImpl.class))
          .extracting(SingularScalarResultImpl::value, InstanceOfAssertFactories.DOUBLE)
          .isEqualTo(expectedResult);
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.QueryServiceDataFactory#localAggregatesSet")
    void querySample_yieldLocalSampleSet(List<DataPoint> dps, String sampleDefinition, String yieldComponent, Double[] expectedResults) {
      var query = """
          WITH SAMPLES:
                      %s
                    YIELD:
                      samples %s""".formatted(sampleDefinition, yieldComponent);

      var result = queryService.query(dps, query);

      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(MultipleScalarResult.class))
          .extracting(MultipleScalarResult::values, InstanceOfAssertFactories.list(Double.class))
          .containsExactly(expectedResults);
    }
  }

  @Nested
  @DisplayName("choose events tests")
  class QueryChooseEvents {
    @ParameterizedTest
    @TsdlTestSources({
        @TsdlTestSource(value = DATA_ROOT + "series2.csv")
    })
    void queryChooseEvents_lowPrecedesHighLiteralEventDefinition_detectsPeriod(List<DataPoint> dps) {
      var query = "USING EVENTS:\nAND(lt(80)) AS low,\nOR(gt(80.0)) AS high\nCHOOSE:\nlow precedes high\nYIELD:\nall periods";

      var result = queryService.query(dps, query);

      assertThat(result.type()).isEqualTo(QueryResultType.PERIOD_SET);
      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlPeriodSet.class))
          .satisfies(periodSet -> {
            assertThat(periodSet.totalPeriods()).isEqualTo(1);
            assertThat(periodSet.periods()).hasSize(1);
            assertThat(periodSet.periods().get(0))
                .isEqualTo(
                    QueryResult.of(0, Instant.parse("2022-12-15T01:21:48.000Z"), Instant.parse("2022-12-15T07:51:48.000Z"))
                );
          });
    }

    @ParameterizedTest
    @TsdlTestSources({
        @TsdlTestSource(value = DATA_ROOT + "series2_gap.csv")
    })
    void queryChooseEvents_lowPrecedesHighLiteralEventDefinitionWithGap_doesNotDetectPeriod(List<DataPoint> dps) {
      // gap is constituted by data point with value of exactly 80.0 right between "low" and "high"
      var query = "USING EVENTS:\nAND(lt(80)) AS low,\nOR(gt(80.0)) AS high\nCHOOSE:\nlow precedes high\nYIELD:\nall periods";

      var result = queryService.query(dps, query);

      assertThat(result.type()).isEqualTo(QueryResultType.PERIOD_SET);
      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlPeriodSet.class))
          .satisfies(periodSet -> {
            assertThat(periodSet.totalPeriods()).isEqualTo(0);
            assertThat(periodSet.periods()).hasSize(0);
            assertThat(periodSet.isEmpty()).isTrue();
          });
    }


    @ParameterizedTest
    @TsdlTestSources({
        @TsdlTestSource(value = DATA_ROOT + "series2.csv")
    })
    void queryChooseEvents_highFollowsLowLiteralEventDefinition_detectsPeriod(List<DataPoint> dps) {
      var query = "USING EVENTS:\nAND(lt(80)) AS low,\nOR(gt(80.0)) AS high\nCHOOSE:\nhigh follows low\nYIELD:\nall periods";

      var result = queryService.query(dps, query);

      assertThat(result.type()).isEqualTo(QueryResultType.PERIOD_SET);
      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlPeriodSet.class))
          .satisfies(periodSet -> {
            assertThat(periodSet.totalPeriods()).isEqualTo(1);
            assertThat(periodSet.periods()).hasSize(1);
            assertThat(periodSet.periods().get(0))
                .isEqualTo(
                    QueryResult.of(0, Instant.parse("2022-12-15T01:21:48.000Z"), Instant.parse("2022-12-15T07:51:48.000Z"))
                );
          });
    }

    @ParameterizedTest
    @TsdlTestSources({
        @TsdlTestSource(value = DATA_ROOT + "series2_gap.csv")
    })
    void queryChooseEvents_highFollowsLowLiteralEventDefinitionWithGap_doesNotDetectPeriod(List<DataPoint> dps) {
      // gap is constituted by data point with value of exactly 80.0 right between "low" and "high"
      var query = "USING EVENTS:\nAND(lt(80)) AS low,\nOR(gt(80.0)) AS high\nCHOOSE:\nhigh follows low\nYIELD:\nall periods";

      var result = queryService.query(dps, query);

      assertThat(result.type()).isEqualTo(QueryResultType.PERIOD_SET);
      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlPeriodSet.class))
          .satisfies(periodSet -> {
            assertThat(periodSet.totalPeriods()).isEqualTo(0);
            assertThat(periodSet.periods()).hasSize(0);
            assertThat(periodSet.isEmpty()).isTrue();
          });
    }

    @ParameterizedTest
    @TsdlTestSources({
        @TsdlTestSource(value = DATA_ROOT + "series2.csv")
    })
    void queryChooseEvents_lowFollowsHighSampleEventDefinition(List<DataPoint> dps) {
      var query = """
          WITH SAMPLES:
                      avg() AS myAvg
                    USING EVENTS:
                      AND(lt(myAvg)) AS low,
                      AND(gt(myAvg)) AS high
                    CHOOSE:
                      low follows high
                    YIELD:
                      all periods""";

      var result = queryService.query(dps, query);

      assertThat(result.type()).isEqualTo(QueryResultType.PERIOD_SET);
      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlPeriodSet.class))
          .satisfies(periodSet -> {
            assertThat(periodSet.totalPeriods()).isEqualTo(2);
            assertThat(periodSet.periods()).hasSize(2);

            assertThat(periodSet.periods().get(0)).isEqualTo(
                QueryResult.of(0, Instant.parse("2022-12-15T02:51:48.000Z"), Instant.parse("2022-12-15T04:36:48.000Z"))
            );
            assertThat(periodSet.periods().get(1)).isEqualTo(
                QueryResult.of(1, Instant.parse("2022-12-15T04:51:48.000Z"), Instant.parse("2022-12-15T09:21:48.000Z"))
            );
          });
    }

    @ParameterizedTest
    @TsdlTestSources({
        @TsdlTestSource(value = DATA_ROOT + "series2.csv")
    })
    void queryChooseEvents_lowFollowsHighSampleEventDefinitionWithPossibleDurationConstraint_reducesNumberOfResultPeriods(List<DataPoint> dps) {
      var query = """
          WITH SAMPLES:
                      avg() AS myAvg
                    USING EVENTS:
                      AND(lt(myAvg)) AS low,
                      AND(gt(myAvg)) FOR [2,] hours AS high
                    CHOOSE:
                      low follows high
                    YIELD:
                      all periods""";

      var result = queryService.query(dps, query);

      assertThat(result.type()).isEqualTo(QueryResultType.PERIOD_SET);
      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlPeriodSet.class))
          .satisfies(periodSet -> {
            assertThat(periodSet.totalPeriods()).isEqualTo(1);
            assertThat(periodSet.periods()).hasSize(1);

            assertThat(periodSet.periods().get(0)).isEqualTo(
                QueryResult.of(0, Instant.parse("2022-12-15T04:51:48.000Z"), Instant.parse("2022-12-15T09:21:48.000Z"))
            );
          });
    }

    @ParameterizedTest
    @TsdlTestSources({
        @TsdlTestSource(value = DATA_ROOT + "series2.csv")
    })
    void queryChooseEvents_lowFollowsHighSampleEventDefinitionWithImpossibleDuration_returnsEmptyPeriodSet(List<DataPoint> dps) {
      var query = """
          WITH SAMPLES:
                      avg() AS myAvg
                    USING EVENTS:
                      AND(lt(myAvg)) FOR [3,] days AS low,
                      AND(gt(myAvg)) FOR [20,] weeks AS high
                    CHOOSE:
                      low follows high
                    YIELD:
                      all periods""";

      var result = queryService.query(dps, query);

      assertThat(result.type()).isEqualTo(QueryResultType.PERIOD_SET);
      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlPeriodSet.class))
          .satisfies(periodSet -> {
            assertThat(periodSet.totalPeriods()).isEqualTo(0);
            assertThat(periodSet.periods()).hasSize(0);
            assertThat(periodSet.isEmpty()).isTrue();
          });
    }

    @ParameterizedTest
    @TsdlTestSources({
        @TsdlTestSource(DATA_ROOT + "series1.csv")
    })
    @TsdlTestVisualization(renderPointShape = false)
    void queryChooseEvents_lowPrecedesHigh_returnsShortestPeriod(List<DataPoint> dps) {
      var query = """
          USING EVENTS:
                      AND(lt(60)) AS low,
                      OR(gt(60)) AS high
                    CHOOSE:
                      low precedes high
                    YIELD:
                      shortest period""";

      var result = queryService.query(dps, query);

      assertThat(result.type()).isEqualTo(QueryResultType.PERIOD);
      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlPeriod.class))
          .isEqualTo(
              QueryResult.of(1, Instant.parse("2022-06-01T19:14:59.306Z"), Instant.parse("2022-06-01T19:29:59.306Z"))
          );
    }

    @ParameterizedTest
    @TsdlTestSources({
        @TsdlTestSource(DATA_ROOT + "series1.csv")
    })
    @TsdlTestVisualization(renderPointShape = false)
    void queryChooseEvents_lowFollowsHigh_returnsLongestPeriod(List<DataPoint> dps) {
      var query = """
          WITH SAMPLES:
                      avg() AS myAvg
                    USING EVENTS:
                      AND(lt(myAvg)) AS low,
                      OR(gt(myAvg)) AS high
                    CHOOSE:
                      low follows high
                    YIELD:
                      longest period""";

      var result = queryService.query(dps, query);

      assertThat(result.type()).isEqualTo(QueryResultType.PERIOD);
      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlPeriod.class))
          .isEqualTo(
              QueryResult.of(2, Instant.parse("2022-06-02T00:44:59.306Z"), Instant.parse("2022-06-02T07:44:59.306Z"))
          );
    }

    @ParameterizedTest
    @TsdlTestSources({
        @TsdlTestSource(DATA_ROOT + "series2.csv")
    })
    void queryChooseEvents_highPrecedesLow_returnsAllMatchingDataPoints(List<DataPoint> dps) {
      var query = """
          USING EVENTS:
                      AND(lt(80)) AS low,
                      OR(gt(80)) AS high
                    CHOOSE:
                      high precedes low
                    YIELD:
                      data points""";

      var result = queryService.query(dps, query);

      assertThat(result.type()).isEqualTo(QueryResultType.DATA_POINTS);
      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlDataPoints.class))
          .isEqualTo(
              QueryResult.of(
                  dps.stream()
                      .filter(dp -> dp.timestamp().isAfter(Instant.parse("2022-12-15T02:36:48.000Z")))
                      .toList()
              )
          );
    }

    @ParameterizedTest
    @TsdlTestSources({
        @TsdlTestSource(DATA_ROOT + "series2.csv")
    })
    void queryChooseEvents_impossibleAllPeriodsChoice_emptyPeriodSet(List<DataPoint> dps) {
      var query = """
          USING EVENTS:
                      AND(lt(-1000)) AS low,
                      OR(gt(2000)) AS high
                    CHOOSE:
                      high precedes low
                    YIELD:
                      all periods""";

      var result = queryService.query(dps, query);

      assertThat(result.type()).isEqualTo(QueryResultType.PERIOD_SET);
      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlPeriodSet.class))
          .extracting(TsdlPeriodSet::totalPeriods, TsdlPeriodSet::periods, TsdlPeriodSet::isEmpty)
          .containsExactly(0, List.of(), true);
    }

    @ParameterizedTest
    @TsdlTestSources({
        @TsdlTestSource(DATA_ROOT + "series2.csv")
    })
    void queryChooseEvents_impossibleLongestPeriodChoice_returnsEmptyPeriod(List<DataPoint> dps) {
      var query = """
          USING EVENTS:
                      AND(lt(-1000)) AS low,
                      OR(gt(2000)) AS high
                    CHOOSE:
                      high precedes low
                    YIELD:
                      longest period""";

      var result = queryService.query(dps, query);

      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlPeriod.class))
          .isNotNull()
          .extracting(QueryResult::type, TsdlPeriod::isEmpty)
          .containsExactly(QueryResultType.PERIOD, true);
    }


    @ParameterizedTest
    @TsdlTestSources({
        @TsdlTestSource(DATA_ROOT + "series2.csv")
    })
    void queryChooseEvents_impossibleShortestPeriodChoice_returnsEmptyPeriod(List<DataPoint> dps) {
      var query = """
          USING EVENTS:
                      AND(lt(-1000)) AS low,
                      OR(gt(2000)) AS high
                    CHOOSE:
                      high precedes low
                    YIELD:
                      shortest period""";

      var result = queryService.query(dps, query);

      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlPeriod.class))
          .isNotNull()
          .extracting(QueryResult::type, TsdlPeriod::isEmpty)
          .containsExactly(QueryResultType.PERIOD, true);
    }

    @ParameterizedTest
    @TsdlTestSources({
        @TsdlTestSource(DATA_ROOT + "series2.csv")
    })
    void queryChooseEvents_impossibleDataPointsChoice_returnsEmptyList(List<DataPoint> dps) {
      var query = """
          USING EVENTS:
                      AND(lt(-1000)) AS low,
                      OR(gt(2000)) AS high
                    CHOOSE:
                      high precedes low
                    YIELD:
                      data points""";

      var result = queryService.query(dps, query);

      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlDataPoints.class))
          .isNotNull()
          .extracting(QueryResult::type, TsdlDataPoints::items)
          .containsExactly(QueryResultType.DATA_POINTS, List.of());
    }

    @ParameterizedTest
    @TsdlTestSources({
        @TsdlTestSource(value = DATA_ROOT + "series3.csv", skipHeaders = 5)
    })
    @TsdlTestVisualization(renderPointShape = false)
    void queryChooseEvents_choiceBetweenFiltersWithDifferentThresholds(List<DataPoint> dps) {
      var query = """
          USING EVENTS:
                      AND(gt(220)) AS high,
                      AND(lt(-20)) AS low
                    CHOOSE:
                      high precedes low
                    YIELD:
                      all periods""";

      var result = queryService.query(dps, query);

      assertThat(result.type()).isEqualTo(QueryResultType.PERIOD_SET);
      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlPeriodSet.class))
          .satisfies(periodSet -> {
            assertThat(periodSet.totalPeriods()).isEqualTo(2);
            assertThat(periodSet.periods()).hasSize(2);

            assertThat(periodSet.periods().get(0)).isEqualTo(
                QueryResult.of(0, Instant.parse("2022-06-10T05:57:58.012Z"), Instant.parse("2022-06-11T06:12:58.012Z"))
            );
            assertThat(periodSet.periods().get(1)).isEqualTo(
                QueryResult.of(1, Instant.parse("2022-06-14T15:42:58.012Z"), Instant.parse("2022-06-15T04:57:58.012Z"))
            );
          });
    }

  }

  @Nested
  @DisplayName("filter tests")
  class QueryFilter {

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.QueryServiceDataFactory#dataPoints_0")
    void queryFilter_lt(List<DataPoint> dataPoints) {
      filterTest("""
          APPLY FILTER:
                      AND(lt(27.25))
                    YIELD: data points""", dataPoints, List.of(dataPoints.get(0)));
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.QueryServiceDataFactory#dataPoints_0")
    void queryFilter_ltSample(List<DataPoint> dataPoints) {
      filterTest("""
          WITH SAMPLES: avg() AS myAvg
                    APPLY FILTER: AND(lt(myAvg))
                    YIELD: data points""", dataPoints, List.of(dataPoints.get(0), dataPoints.get(1)));
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.QueryServiceDataFactory#dataPoints_0")
    @TsdlTestVisualization(dateAxisFormat = TsdlTestVisualization.PRECISE_AXIS_FORMAT)
    void queryFilter_gt(List<DataPoint> dataPoints) {
      filterTest("""
          APPLY FILTER:
                      AND(gt(27.24))
                    YIELD: data points""", dataPoints, List.of(dataPoints.get(1), dataPoints.get(2)));
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.QueryServiceDataFactory#dataPoints_0")
    @TsdlTestVisualization(dateAxisFormat = TsdlTestVisualization.PRECISE_AXIS_FORMAT)
    void queryFilter_gtSample(List<DataPoint> dataPoints) {
      filterTest("""
          WITH SAMPLES: avg() AS myAvg
                    APPLY FILTER: AND(gt(myAvg))
                    YIELD: data points""", dataPoints, List.of(dataPoints.get(2)));
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.QueryServiceDataFactory#dataPoints_0")
    void queryFilter_gtAndLt(List<DataPoint> dataPoints) {
      filterTest("""
          APPLY FILTER:
                      AND( gt(25.75), lt(75) )
                    YIELD: data points""", dataPoints, List.of(dataPoints.get(1)));
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.QueryServiceDataFactory#dataPoints_0")
    void queryFilter_gtAndLtSample(List<DataPoint> dataPoints) {
      filterTest("""
          WITH SAMPLES: avg() AS myAvg
                    APPLY FILTER:
                      AND( gt(myAvg), lt(76) )
                    YIELD: data points""", dataPoints, List.of(dataPoints.get(2)));
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.QueryServiceDataFactory#dataPoints_0")
    void queryFilter_gtOrLt(List<DataPoint> dataPoints) {
      filterTest("""
          APPLY FILTER:
                      OR( gt(75), lt(26) )
                    YIELD: data points""", dataPoints, List.of(dataPoints.get(0), dataPoints.get(2)));
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.QueryServiceDataFactory#dataPoints_0")
    void queryFilter_gtOrLtSample(List<DataPoint> dataPoints) {
      filterTest("""
          WITH SAMPLES: avg() AS myAvg
                    APPLY FILTER:
                      OR( gt(myAvg), lt(21) )
                    YIELD: data points""", dataPoints, List.of(dataPoints.get(2)));
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.QueryServiceDataFactory#dataPoints_0")
    void queryFilter_gtAndNotLt(List<DataPoint> dataPoints) {
      filterTest("""
          APPLY FILTER:
                      AND( gt(25.75), NOT(lt(28)) )
                    YIELD: data points""", dataPoints, List.of(dataPoints.get(2)));
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.QueryServiceDataFactory#dataPoints_0")
    void queryFilter_gtAndNotLtSample(List<DataPoint> dataPoints) {
      filterTest("""
          WITH SAMPLES: avg() AS myAvg
                    APPLY FILTER:
                      AND( gt(25), NOT(lt(myAvg)) )
                    YIELD: data points""", dataPoints, List.of(dataPoints.get(2)));
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.QueryServiceDataFactory#dataPoints_0")
    void queryFilter_notGtOrLt(List<DataPoint> dataPoints) {
      filterTest("""
          APPLY FILTER:
                      OR( NOT(gt(27.25)), lt(26) )
                    YIELD: data points""", dataPoints, List.of(dataPoints.get(0), dataPoints.get(1)));
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.QueryServiceDataFactory#dataPoints_0")
    void queryFilter_notGtOrLtSample(List<DataPoint> dataPoints) {
      filterTest("""
          WITH SAMPLES: avg() AS myAvg
                    APPLY FILTER:
                      OR( NOT(gt(myAvg)), lt(27) )
                    YIELD: data points""", dataPoints, List.of(dataPoints.get(0), dataPoints.get(1)));
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.QueryServiceDataFactory#dataPoints_0")
    void queryFilter_impossibleQuery(List<DataPoint> dataPoints) {
      filterTest("""
          APPLY FILTER:
                      AND( gt(100), lt(100) )
                    YIELD: data points""", dataPoints, List.of());
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.QueryServiceDataFactory#dataPoints_0")
    void queryFilter_impossibleQuerySample(List<DataPoint> dataPoints) {
      filterTest("""
          WITH SAMPLES: avg() AS myAvg
                    APPLY FILTER:
                      AND( gt(myAvg), lt(myAvg) )
                    YIELD: data points""", dataPoints, List.of());
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.QueryServiceDataFactory#dataPoints_0")
    void queryFilter_trivialQuery(List<DataPoint> dataPoints) {
      filterTest("""
          APPLY FILTER:
                      OR( gt(100), lt(100) )
                    YIELD: data points""", dataPoints, List.of(dataPoints.get(0), dataPoints.get(1), dataPoints.get(2)));
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.QueryServiceDataFactory#dataPoints_0")
    void queryFilter_trivialQuerySample(List<DataPoint> dataPoints) {
      filterTest("""
          WITH SAMPLES: avg() AS myAvg
                    APPLY FILTER:
                      OR( gt(myAvg), lt(myAvg) )
                    YIELD: data points""", dataPoints, List.of(dataPoints.get(0), dataPoints.get(1), dataPoints.get(2)));
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.QueryServiceDataFactory#dataPoints_0")
    void queryFilter_before(List<DataPoint> dataPoints) {
      filterTest("APPLY FILTER: AND(before(\"2022-05-24T20:36:44.234Z\")) YIELD: data points", dataPoints,
          List.of(dataPoints.get(0), dataPoints.get(1)));
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.QueryServiceDataFactory#dataPoints_0")
    void queryFilter_beforeImpossible(List<DataPoint> dataPoints) {
      filterTest("APPLY FILTER: AND(before(\"2021-05-24T20:36:44.234Z\")) YIELD: data points", dataPoints, List.of());
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.QueryServiceDataFactory#dataPoints_0")
    void queryFilter_after(List<DataPoint> dataPoints) {
      filterTest("APPLY FILTER: AND(after(\"2022-05-24T20:36:44.233Z\")) YIELD: data points", dataPoints, List.of(dataPoints.get(2)));
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.QueryServiceDataFactory#dataPoints_0")
    void queryFilter_afterImpossible(List<DataPoint> dataPoints) {
      filterTest("APPLY FILTER: AND(after(\"2022-05-24T20:36:44.234Z\")) YIELD: data points", dataPoints, List.of());
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.QueryServiceDataFactory#dataPoints_0")
    void queryFilter_notAfterAndAfter(List<DataPoint> dataPoints) {
      filterTest("APPLY FILTER: AND(NOT(after(\"2022-05-24T20:33:45.234Z\")), after(\"2022-05-24T20:33:45.000Z\")) YIELD: data points", dataPoints,
          List.of(dataPoints.get(1)));
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.QueryServiceDataFactory#dataPoints_0")
    void queryFilter_notBeforeOrBefore(List<DataPoint> dataPoints) {
      filterTest("APPLY FILTER: OR(NOT(before(\"2022-05-24T20:36:44.234Z\")), before(\"2022-05-24T20:33:45.234Z\")) YIELD: data points", dataPoints,
          List.of(dataPoints.get(0), dataPoints.get(2)));
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.QueryServiceDataFactory#dataPoints_0")
    void queryFilter_mixValueAndTimeFilters(List<DataPoint> dataPoints) {
      filterTest("WITH SAMPLES: max() AS myMax APPLY FILTER: AND(NOT(before(\"2022-05-24T20:33:45.234Z\")), lt(myMax)) YIELD: data points",
          dataPoints, List.of(dataPoints.get(1)));
    }

    private void filterTest(String query, List<DataPoint> input, List<DataPoint> expectedResult) {
      var result = queryService.query(input, query);

      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlDataPoints.class))
          .extracting(TsdlDataPoints::items)
          .usingRecursiveComparison()
          .isEqualTo(expectedResult);
    }
  }

  @Nested
  @DisplayName("integration tests")
  class QueryIntegration {
    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.QueryServiceDataFactory#dataPoints_0")
    void queryIntegration_queryWithOnlySampleDeclarations_returnsAllDataPoints(List<DataPoint> dataPoints) {
      var query = "WITH SAMPLES: avg() AS s1, min() AS s2, max() AS s3, sum() AS s4\n"
          + "          YIELD: data points";

      var expectedItems = List.of(dataPoints.get(0), dataPoints.get(1), dataPoints.get(2));

      var result = queryService.query(dataPoints, query);

      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlDataPoints.class))
          .extracting(TsdlDataPoints::items)
          .usingRecursiveComparison()
          .isEqualTo(expectedItems);
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.QueryServiceDataFactory#dataPoints_0")
    void queryIntegration_emptyQuery_returnsAllDataPoints(List<DataPoint> dataPoints) {
      var query = "YIELD: data points";

      var expectedItems = List.of(dataPoints.get(0), dataPoints.get(1), dataPoints.get(2));

      var result = queryService.query(dataPoints, query);

      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlDataPoints.class))
          .extracting(TsdlDataPoints::items)
          .usingRecursiveComparison()
          .isEqualTo(expectedItems);
    }

    @ParameterizedTest
    @TsdlTestSources({
        @TsdlTestSource(value = DATA_ROOT + "series0.csv")
    })
    @TsdlTestVisualization(renderPointShape = false)
    void queryIntegration_filterBeforeEventChoiceCutsOffPeriodCorrectly(List<DataPoint> dps) {
      var queryWithoutFilter = """
          USING EVENTS: AND(lt(170)) AS low, AND(gt(170)) AS high
                    CHOOSE: low follows high
                    YIELD: all periods""";
      var queryWithFilter = "APPLY FILTER: AND(NOT(lt(130)))\n" + queryWithoutFilter;

      var resultWithoutFilter = queryService.query(dps, queryWithoutFilter);
      var resultWithFilter = queryService.query(dps, queryWithFilter);

      // test data visualization shows that periods are the same, except the third one where the filter cuts off some of the last values
      assertThat(resultWithoutFilter)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlPeriodSet.class))
          .extracting(TsdlPeriodSet::totalPeriods)
          .isEqualTo(3);
      assertThat(resultWithFilter)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlPeriodSet.class))
          .extracting(TsdlPeriodSet::totalPeriods)
          .isEqualTo(3);

      var periodsWithoutFilter = (TsdlPeriodSet) resultWithoutFilter;
      var periodsWitFilter = (TsdlPeriodSet) resultWithFilter;

      // first two periods are the same
      assertThat(periodsWithoutFilter)
          .extracting(ps -> ps.periods().get(0), ps -> ps.periods().get(1))
          .containsExactly(periodsWitFilter.periods().get(0), periodsWitFilter.periods().get(1));

      // third one differs only in end
      assertThat(periodsWithoutFilter.periods().get(2).start()).isEqualTo(periodsWitFilter.periods().get(2).start());
      assertThat(periodsWithoutFilter.periods().get(2).end()).isEqualTo(Instant.parse("2022-06-03T09:35:33.164Z"));
      assertThat(periodsWitFilter.periods().get(2).end()).isEqualTo(Instant.parse("2022-06-03T01:35:33.164Z"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"data points", "all periods", "shortest period", "longest period", "sample m1", "sample m2", "samples m1", "samples m2",
        "samples m1, m2", "samples m2,m1"})
    void queryIntegration_queryWithEchos_collectsLogMessages(String yield) {
      var query = """
          WITH SAMPLES: avg() AS m1 -> echo(3), max() AS m2 -> echo(0)
                    USING EVENTS: AND(lt(m1)) AS low, OR(gt(m1)) AS high
                    CHOOSE: low precedes high
                    YIELD: %s""".formatted(yield);

      var input = List.of(
          DataPoint.of(Instant.now(), 1.),
          DataPoint.of(Instant.now(), 2.),
          DataPoint.of(Instant.now(), 3.)
      );
      var result = queryService.query(input, query);

      assertThat(result.logs())
          .hasSize(2)
          .extracting(TsdlLogEvent::message)
          .isEqualTo(List.of(
              "sample 'm1' of 'avg' aggregator := 2.000",
              "sample 'm2' of 'max' aggregator := 3"
          ));
    }

    @ParameterizedTest
    @TsdlTestSources({
        @TsdlTestSource(value = DATA_ROOT + "series4.csv", skipHeaders = 6),
        @TsdlTestSource(value = DATA_ROOT + "series4_result.csv"),
    })
    @TsdlTestVisualization(renderPointShape = false, dateAxisFormat = "dd/MM HH:mm")
    void queryIntegration_temporalAndValueFilterBasedOnLocalAverage_cutsOffCorrectly(List<DataPoint> dps, List<DataPoint> expectedResult) {
      var query = """
          WITH SAMPLES: avg("2022-07-22T17:30:00.000Z", "2022-07-22T23:55:00.000Z") AS localAvg -> echo(4)
          APPLY FILTER: AND(
                    NOT(before("2022-07-22T17:30:00.000Z")),
                    NOT(after("2022-07-22T23:55:00.000Z")),
                    gt(localAvg)
                  )
          YIELD: data points""";

      var result = queryService.query(dps, query);

      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlDataPoints.class))
          .extracting(TsdlDataPoints::items, InstanceOfAssertFactories.list(DataPoint.class))
          .isEqualTo(expectedResult);
    }
  }
}
