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
import org.tsdl.infrastructure.api.QueryService;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.infrastructure.model.QueryResultType;
import org.tsdl.infrastructure.model.TsdlDataPoints;
import org.tsdl.infrastructure.model.TsdlPeriod;
import org.tsdl.infrastructure.model.TsdlPeriodSet;
import org.tsdl.testutil.creation.provider.TsdlTestSource;
import org.tsdl.testutil.creation.provider.TsdlTestSources;
import org.tsdl.testutil.visualization.api.TsdlTestVisualization;
import org.tsdl.testutil.visualization.impl.TsdlTestVisualizer;

@ExtendWith(TsdlTestVisualizer.class)
class TsdlQueryServiceTest {
  private static final String DATA_ROOT = "data/query/";
  private static final QueryService queryService = new TsdlQueryService();

  @Nested
  @DisplayName("choose events tests")
  class QueryChooseEvents {
    @ParameterizedTest
    @TsdlTestSources({
        @TsdlTestSource(value = DATA_ROOT + "series2.csv")
    })
    void queryChooseEvents_lowPrecedesHighLiteralEventDefinition(List<DataPoint> dps) {
      var query = """
          USING EVENTS:
            AND(lt(80)) AS low,
            OR(gt(80.0)) AS high
          CHOOSE:
            low precedes high
          YIELD:
            all periods
          """;

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
        @TsdlTestSource(value = DATA_ROOT + "series2.csv")
    })
    void queryChooseEvents_lowFollowsHighSampleEventDefinition(List<DataPoint> dps) {
      var query = """
          WITH SAMPLES:
            avg(_input) AS myAvg
          USING EVENTS:
            AND(lt(myAvg)) AS low,
            AND(gt(myAvg)) AS high
          CHOOSE:
            low follows high
          YIELD:
            all periods
          """;

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
            shortest period
          """;

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
            avg(_input) AS myAvg
          USING EVENTS:
            AND(lt(myAvg)) AS low,
            OR(gt(myAvg)) AS high
          CHOOSE:
            low follows high
          YIELD:
            longest period
          """;

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
            data points
          """;

      var result = queryService.query(dps, query);

      assertThat(result.type()).isEqualTo(QueryResultType.DATA_POINTS);
      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlDataPoints.class))
          .isEqualTo(
              QueryResult.of(
                  dps.stream()
                      .filter(dp -> dp.getTimestamp().isAfter(Instant.parse("2022-12-15T02:36:48.000Z")))
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
            all periods
          """;

      var result = queryService.query(dps, query);

      assertThat(result.type()).isEqualTo(QueryResultType.PERIOD_SET);
      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlPeriodSet.class))
          .extracting(TsdlPeriodSet::totalPeriods, TsdlPeriodSet::periods)
          .containsExactly(0, List.of());
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
            longest period
          """;

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
            shortest period
          """;

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
            data points
          """;

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
            all periods
          """;

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
    @MethodSource("org.tsdl.implementation.evaluation.stub.DataPointDataFactory#dataPoints_0")
    void queryFilter_lt(List<DataPoint> dataPoints) {
      var query = """
          FILTER:
            AND(lt(27.25))
          YIELD: data points
          """;

      var expectedItems = List.of(dataPoints.get(0));

      var result = queryService.query(dataPoints, query);

      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlDataPoints.class))
          .extracting(TsdlDataPoints::items)
          .usingRecursiveComparison()
          .isEqualTo(expectedItems);
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.DataPointDataFactory#dataPoints_0")
    void queryFilter_ltSample(List<DataPoint> dataPoints) {
      var query = """
          WITH SAMPLES: avg(_input) AS myAvg
          FILTER: AND(lt(myAvg))
          YIELD: data points
          """;

      var expectedItems = List.of(dataPoints.get(0), dataPoints.get(1));

      var result = queryService.query(dataPoints, query);

      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlDataPoints.class))
          .extracting(TsdlDataPoints::items)
          .usingRecursiveComparison()
          .isEqualTo(expectedItems);
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.DataPointDataFactory#dataPoints_0")
    @TsdlTestVisualization(dateAxisFormat = TsdlTestVisualization.PRECISE_AXIS_FORMAT)
    void queryFilter_gt(List<DataPoint> dataPoints) {
      var query = """
          FILTER:
            AND(gt(27.24))
          YIELD: data points
          """;

      var expectedItems = List.of(dataPoints.get(1), dataPoints.get(2));

      var result = queryService.query(dataPoints, query);

      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlDataPoints.class))
          .extracting(TsdlDataPoints::items)
          .usingRecursiveComparison()
          .isEqualTo(expectedItems);
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.DataPointDataFactory#dataPoints_0")
    @TsdlTestVisualization(dateAxisFormat = TsdlTestVisualization.PRECISE_AXIS_FORMAT)
    void queryFilter_gtSample(List<DataPoint> dataPoints) {
      var query = """
          WITH SAMPLES: avg(_input) AS myAvg
          FILTER: AND(gt(myAvg))
          YIELD: data points
          """;

      var expectedItems = List.of(dataPoints.get(2));

      var result = queryService.query(dataPoints, query);

      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlDataPoints.class))
          .extracting(TsdlDataPoints::items)
          .usingRecursiveComparison()
          .isEqualTo(expectedItems);
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.DataPointDataFactory#dataPoints_0")
    void queryFilter_gtAndLt(List<DataPoint> dataPoints) {
      var query = """
          FILTER:
            AND( gt(25.75), lt(75) )
          YIELD: data points
          """;

      var expectedItems = List.of(dataPoints.get(1));

      var result = queryService.query(dataPoints, query);

      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlDataPoints.class))
          .extracting(TsdlDataPoints::items)
          .usingRecursiveComparison()
          .isEqualTo(expectedItems);
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.DataPointDataFactory#dataPoints_0")
    void queryFilter_gtAndLtSample(List<DataPoint> dataPoints) {
      var query = """
          WITH SAMPLES: avg(_input) AS myAvg
          FILTER:
            AND( gt(myAvg), lt(76) )
          YIELD: data points
          """;

      var expectedItems = List.of(dataPoints.get(2));

      var result = queryService.query(dataPoints, query);

      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlDataPoints.class))
          .extracting(TsdlDataPoints::items)
          .usingRecursiveComparison()
          .isEqualTo(expectedItems);
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.DataPointDataFactory#dataPoints_0")
    void queryFilter_gtOrLt(List<DataPoint> dataPoints) {
      var query = """
          FILTER:
            OR( gt(75), lt(26) )
          YIELD: data points
          """;

      var expectedItems = List.of(dataPoints.get(0), dataPoints.get(2));

      var result = queryService.query(dataPoints, query);

      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlDataPoints.class))
          .extracting(TsdlDataPoints::items)
          .usingRecursiveComparison()
          .isEqualTo(expectedItems);
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.DataPointDataFactory#dataPoints_0")
    void queryFilter_gtOrLtSample(List<DataPoint> dataPoints) {
      var query = """
          WITH SAMPLES: avg(_input) AS myAvg
          FILTER:
            OR( gt(myAvg), lt(21) )
          YIELD: data points
          """;

      var expectedItems = List.of(dataPoints.get(2));

      var result = queryService.query(dataPoints, query);

      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlDataPoints.class))
          .extracting(TsdlDataPoints::items)
          .usingRecursiveComparison()
          .isEqualTo(expectedItems);
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.DataPointDataFactory#dataPoints_0")
    void queryFilter_gtAndNotLt(List<DataPoint> dataPoints) {
      var query = """
          FILTER:
            AND( gt(25.75), NOT(lt(28)) )
          YIELD: data points
          """;

      var expectedItems = List.of(dataPoints.get(2));

      var result = queryService.query(dataPoints, query);

      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlDataPoints.class))
          .extracting(TsdlDataPoints::items)
          .usingRecursiveComparison()
          .isEqualTo(expectedItems);
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.DataPointDataFactory#dataPoints_0")
    void queryFilter_gtAndNotLtSample(List<DataPoint> dataPoints) {
      var query = """
          WITH SAMPLES: avg(_input) AS myAvg
          FILTER:
            AND( gt(25), NOT(lt(myAvg)) )
          YIELD: data points
          """;

      var expectedItems = List.of(dataPoints.get(2));

      var result = queryService.query(dataPoints, query);

      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlDataPoints.class))
          .extracting(TsdlDataPoints::items)
          .usingRecursiveComparison()
          .isEqualTo(expectedItems);
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.DataPointDataFactory#dataPoints_0")
    void queryFilter_notGtOrLt(List<DataPoint> dataPoints) {
      var query = """
          FILTER:
            OR( NOT(gt(27.25)), lt(26) )
          YIELD: data points
          """;

      var expectedItems = List.of(dataPoints.get(0), dataPoints.get(1));

      var result = queryService.query(dataPoints, query);

      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlDataPoints.class))
          .extracting(TsdlDataPoints::items)
          .usingRecursiveComparison()
          .isEqualTo(expectedItems);
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.DataPointDataFactory#dataPoints_0")
    void queryFilter_notGtOrLtSample(List<DataPoint> dataPoints) {
      var query = """
          WITH SAMPLES: avg(_input) AS myAvg
          FILTER:
            OR( NOT(gt(myAvg)), lt(27) )
          YIELD: data points
          """;

      var expectedItems = List.of(dataPoints.get(0), dataPoints.get(1));

      var result = queryService.query(dataPoints, query);

      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlDataPoints.class))
          .extracting(TsdlDataPoints::items)
          .usingRecursiveComparison()
          .isEqualTo(expectedItems);
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.DataPointDataFactory#dataPoints_0")
    void queryFilter_impossibleQuery(List<DataPoint> dataPoints) {
      var query = """
          FILTER:
            AND( gt(100), lt(100) )
          YIELD: data points
          """;

      var expectedItems = List.of();

      var result = queryService.query(dataPoints, query);

      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlDataPoints.class))
          .extracting(TsdlDataPoints::items)
          .usingRecursiveComparison()
          .isEqualTo(expectedItems);
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.DataPointDataFactory#dataPoints_0")
    void queryFilter_impossibleQuerySample(List<DataPoint> dataPoints) {
      var query = """
          WITH SAMPLES: avg(_input) AS myAvg
          FILTER:
            AND( gt(myAvg), lt(myAvg) )
          YIELD: data points
          """;

      var expectedItems = List.of();

      var result = queryService.query(dataPoints, query);

      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlDataPoints.class))
          .extracting(TsdlDataPoints::items)
          .usingRecursiveComparison()
          .isEqualTo(expectedItems);
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.DataPointDataFactory#dataPoints_0")
    void queryFilter_trivialQuery(List<DataPoint> dataPoints) {
      var query = """
          FILTER:
            OR( gt(100), lt(100) )
          YIELD: data points
          """;

      var expectedItems = List.of(dataPoints.get(0), dataPoints.get(1), dataPoints.get(2));

      var result = queryService.query(dataPoints, query);

      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlDataPoints.class))
          .extracting(TsdlDataPoints::items)
          .usingRecursiveComparison()
          .isEqualTo(expectedItems);
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.DataPointDataFactory#dataPoints_0")
    void queryFilter_trivialQuerySample(List<DataPoint> dataPoints) {
      var query = """
          WITH SAMPLES: avg(_input) AS myAvg
          FILTER:
            OR( gt(myAvg), lt(myAvg) )
          YIELD: data points
          """;

      var expectedItems = List.of(dataPoints.get(0), dataPoints.get(1), dataPoints.get(2));

      var result = queryService.query(dataPoints, query);

      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlDataPoints.class))
          .extracting(TsdlDataPoints::items)
          .usingRecursiveComparison()
          .isEqualTo(expectedItems);
    }
  }

  @Nested
  @DisplayName("integration tests")
  class QueryIntegration {
    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.DataPointDataFactory#dataPoints_0")
    void queryIntegration_queryWithOnlySampleDeclarations_returnsAllDataPoints(List<DataPoint> dataPoints) {
      var query = """
          WITH SAMPLES: avg(_input) AS s1, min(_input) AS s2, max(_input) AS s3, sum(_input) AS s4
          YIELD: data points
          """;

      var expectedItems = List.of(dataPoints.get(0), dataPoints.get(1), dataPoints.get(2));

      var result = queryService.query(dataPoints, query);

      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlDataPoints.class))
          .extracting(TsdlDataPoints::items)
          .usingRecursiveComparison()
          .isEqualTo(expectedItems);
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.DataPointDataFactory#dataPoints_0")
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
          YIELD: all periods
          """;
      var queryWithFilter = "FILTER: AND(NOT(lt(130)))\n" + queryWithoutFilter;

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
  }
}
