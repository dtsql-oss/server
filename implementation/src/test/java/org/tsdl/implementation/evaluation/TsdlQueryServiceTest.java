package org.tsdl.implementation.evaluation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withPrecision;

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
import org.tsdl.infrastructure.common.TsdlTimeUnit;
import org.tsdl.infrastructure.common.TsdlUtil;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.infrastructure.model.MultipleScalarResult;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.infrastructure.model.QueryResultType;
import org.tsdl.infrastructure.model.SingularScalarResult;
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
          .isEqualTo(expectedResult, withPrecision(0.001d));
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.QueryServiceDataFactory#globalAggregates")
    void querySample_yieldGlobalSampleAndLocalWithoutBounds_isEqual(List<DataPoint> dps, String sampleDefinition, Double expectedResult) {
      var queryGlobal = "WITH SAMPLES: %s AS s1  YIELD: sample s1".formatted(sampleDefinition);
      var queryLocal = queryGlobal.replaceAll("\\(\\)", "(\"\", \"\")");

      var globalResult = (SingularScalarResult) queryService.query(dps, queryGlobal);
      var localResult = (SingularScalarResult) queryService.query(dps, queryLocal);

      assertThat(globalResult.value())
          .isEqualTo(expectedResult, withPrecision(0.001d))
          .isEqualTo(localResult.value(), withPrecision(0.001d));
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
          .extracting(r -> r.values().stream().mapToDouble(Double::doubleValue).toArray(), InstanceOfAssertFactories.DOUBLE_ARRAY)
          .containsExactly(expectedResults, withPrecision(0.001d));
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.QueryServiceDataFactory#localAggregates")
    void querySample_yieldLocalSample(List<DataPoint> dps, String sampleDefinition, Double expectedResult) {
      var query = "WITH SAMPLES: %s AS s1  YIELD: sample s1".formatted(sampleDefinition);

      var result = queryService.query(dps, query);

      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(SingularScalarResultImpl.class))
          .extracting(SingularScalarResultImpl::value, InstanceOfAssertFactories.DOUBLE)
          .isEqualTo(expectedResult, withPrecision(0.001d));
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
          .extracting(r -> r.values().stream().mapToDouble(Double::doubleValue).toArray(), InstanceOfAssertFactories.DOUBLE_ARRAY)
          .containsExactly(expectedResults, withPrecision(0.001d));
    }

    @ParameterizedTest
    @TsdlTestSources(
        @TsdlTestSource(value = DATA_ROOT + "series5.csv")
    )
    void querySample_yieldGlobalIntegralWithRegularSampling(List<DataPoint> dps) {
      var query = "WITH SAMPLES: integral() AS myIntegral YIELD: sample myIntegral";

      var result = queryService.query(dps, query);

      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(SingularScalarResult.class))
          .extracting(SingularScalarResult::value, InstanceOfAssertFactories.DOUBLE)
          .isEqualTo(50175d);
    }

    @ParameterizedTest
    @TsdlTestSources(
        @TsdlTestSource(value = DATA_ROOT + "series7.csv")
    )
    void querySample_yieldGlobalIntegralWithIrregularSampling(List<DataPoint> dps) {
      var query = "WITH SAMPLES: integral() AS myIntegral YIELD: sample myIntegral";

      var result = queryService.query(dps, query);

      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(SingularScalarResult.class))
          .extracting(SingularScalarResult::value, InstanceOfAssertFactories.DOUBLE)
          .isEqualTo(123_525d);
    }

    @ParameterizedTest
    @TsdlTestSources(
        @TsdlTestSource(value = DATA_ROOT + "series6.csv")
    )
    void querySample_yieldLocalIntegralWithRegularSampling(List<DataPoint> dps) {
      var query = "WITH SAMPLES: integral(\"2022-07-18T12:45:00Z\", \"2022-07-18T13:45:00Z\") AS myIntegral YIELD: sample myIntegral";

      var result = queryService.query(dps, query);

      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(SingularScalarResult.class))
          .extracting(SingularScalarResult::value, InstanceOfAssertFactories.DOUBLE)
          .isEqualTo(50175d);
    }

    @ParameterizedTest
    @TsdlTestSources(
        @TsdlTestSource(value = DATA_ROOT + "series8.csv")
    )
    void querySample_yieldLocalIntegralWithIrregularSampling(List<DataPoint> dps) {
      var query = "WITH SAMPLES: integral(\"2022-07-18T12:45:00.000Z\", \"2022-07-18T15:15:00.000Z\") AS myIntegral YIELD: sample myIntegral";

      var result = queryService.query(dps, query);

      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(SingularScalarResult.class))
          .extracting(SingularScalarResult::value, InstanceOfAssertFactories.DOUBLE)
          .isEqualTo(123_525d);
    }

    @ParameterizedTest
    @TsdlTestVisualization(skipVisualization = true)
    @TsdlTestSources(
        @TsdlTestSource(value = DATA_ROOT + "series10.csv", skipHeaders = 5)
    )
    void querySample_yieldTemporalAggregatesOfOnePeriod(List<DataPoint> dps) {
      var query = """
          WITH SAMPLES:
            avg_t(minutes, "%1$s") AS s1,
            max_t(seconds, "%1$s") AS s2,
            min_t( hours,
                   "%1$s" ) AS s3,
            sum_t(millis, "%1$s") AS s4,
            count_t("%1$s") AS s5,
            stddev_t(weeks, "%1$s") AS s6
                    
          YIELD: samples s1, s2, s3, s4, s5, s6
          """.formatted("2022-08-08T10:35:48.932Z/2022-08-08T12:35:48.932Z");

      var queryResult = queryService.query(dps, query);

      // period has length of
      // 7_200_000 millis, 7200 s, 120 min, 2 h, 0.0833333 days, 0.011904761 weeks
      assertThat(queryResult)
          .asInstanceOf(InstanceOfAssertFactories.type(MultipleScalarResult.class))
          .extracting(result -> result.values().stream().mapToDouble(Double::doubleValue).toArray(), InstanceOfAssertFactories.DOUBLE_ARRAY)
          .containsExactly(new Double[] {
                  //CHECKSTYLE.OFF: Indentation - false positive or IntelliJ auto-format configuration problem. to be fixed later
                  120.0, // avg
                  7200.0, // max
                  2.0, // min
                  7_200_000.0, // sum
                  1.0, // count
                  0.0 // stddev
                  //CHECKSTYLE.ON: Indentation
              },
              withPrecision(0.00000001)
          );
    }

    @ParameterizedTest
    @TsdlTestVisualization(skipVisualization = true)
    @TsdlTestSources(
        @TsdlTestSource(value = DATA_ROOT + "series10.csv", skipHeaders = 5)
    )
    void querySample_yieldTemporalAggregatesOfThreePeriods(List<DataPoint> dps) {
      var query = """
          WITH SAMPLES:
            avg_t(minutes, "%1$s", "%2$s", "%3$s") AS s1,
            max_t(seconds, "%1$s", "%2$s", "%3$s") AS s2,
            min_t( hours,
                   "%1$s" , "%2$s", "%3$s" ) AS s3,
            sum_t(millis, "%1$s", "%2$s", "%3$s") AS s4,
            count_t("%1$s", "%2$s", "%3$s") AS s5,
            stddev_t(weeks, "%1$s", "%2$s", "%3$s") AS s6
                    
          YIELD: samples s1, s2, s3, s4, s5, s6
          """.formatted(
          "2022-08-08T10:35:48.932Z/2022-08-08T12:35:48.932Z", // [0]
          "2022-08-08T13:50:48.932Z/2022-08-08T14:20:48.932Z", // [1]
          "2022-08-08T22:20:48.932Z/2022-08-09T12:50:48.932Z" // [2]
      );

      var queryResult = queryService.query(dps, query);

      // periods have length of
      // [0]: 7_200_000 millis, 7200 s, 120 min, 2 h, 0.0833333 days, 0.011904761 weeks
      // [1]: 1_800_000 millis, 1800 s, 30 min, 0.5 h, 0.0208333333 days, 0.00297619 weeks
      // [2]: 52_200_000 millis, 52200 s, 870 min, 14.5 h, 0.604166666 days, 0.086309523
      assertThat(queryResult)
          .asInstanceOf(InstanceOfAssertFactories.type(MultipleScalarResult.class))
          .extracting(result -> result.values().stream().mapToDouble(Double::doubleValue).toArray(), InstanceOfAssertFactories.DOUBLE_ARRAY)
          .containsExactly(new Double[] {
                  //CHECKSTYLE.OFF: Indentation - false positive or IntelliJ auto-format configuration problem. to be fixed later
                  340.0, // avg
                  52200.0, // max
                  0.5, // min
                  61_200_000.0, // sum
                  3.0, // count
                  0.0373574808 // stddev
                  //CHECKSTYLE.ON: Indentation
              },
              withPrecision(0.00000001)
          );
    }

    @ParameterizedTest
    @TsdlTestVisualization(skipVisualization = true)
    @TsdlTestSources(
        @TsdlTestSource(value = DATA_ROOT + "series10.csv", skipHeaders = 5)
    )
    void querySample_yieldTemporalAggregatesOfDifferentPeriodLists(List<DataPoint> dps) {
      var query = """
          WITH SAMPLES:
            avg_t(minutes, "%1$s", "%2$s", "%3$s") AS s1,
            sum_t(seconds, "%4$s", "%5$s") AS s3,
            stddev_t(weeks, "%1$s", "%2$s", "%3$s") AS s2
                    
          YIELD: samples s1, s2, s3
          """.formatted(
          "2022-08-08T10:35:48.932Z/2022-08-08T12:35:48.932Z", // [0]
          "2022-08-08T13:50:48.932Z/2022-08-08T14:20:48.932Z", // [1]
          "2022-08-08T22:20:48.932Z/2022-08-09T12:50:48.932Z", // [2]
          "2022-08-09T09:20:48.932Z/2022-08-09T12:05:48.932Z", // [3]
          "2022-08-08T08:50:48.932Z/2022-08-08T16:50:48.932Z" // [4]
      );

      var queryResult = queryService.query(dps, query);

      // periods have length of
      // [0]: 7_200_000 millis, 7200 s, 120 min, 2 h, 0.0833333 days, 0.011904761 weeks
      // [1]: 1_800_000 millis, 1800 s, 30 min, 0.5 h, 0.0208333333 days, 0.00297619 weeks
      // [2]: 52_200_000 millis, 52200 s, 870 min, 14.5 h, 0.604166666 days, 0.086309523 weeks
      // [3]: 9_900_000 millis, 9900 s, 165 min, 2.75 h, 0.114583333 days, 0.016369047 weeks
      // [4]: 28_800_000 millis, 28800 s, 480 min, 8 h, 0.3333333333333 days, 0.047619047 weeks
      assertThat(queryResult)
          .asInstanceOf(InstanceOfAssertFactories.type(MultipleScalarResult.class))
          .extracting(result -> result.values().stream().mapToDouble(Double::doubleValue).toArray(), InstanceOfAssertFactories.DOUBLE_ARRAY)
          .containsExactly(new Double[] {
                  //CHECKSTYLE.OFF: Indentation - false positive or IntelliJ auto-format configuration problem. to be fixed later
                  340.0, // avg
                  0.0373574808, // stddev
                  38700.0 // sum
                  //CHECKSTYLE.ON: Indentation
              },
              withPrecision(0.00000001)
          );
    }
  }

  @Nested
  @DisplayName("event tests")
  class QueryEvent {
    @ParameterizedTest
    @TsdlTestSources(
        @TsdlTestSource(value = DATA_ROOT + "series11.csv", skipHeaders = 5)
    )
    @TsdlTestVisualization(renderPointShape = false)
    void queryEvent_aroundAbsolute_detectsPeriods(List<DataPoint> dps) {
      // [138, 178]
      var query = """
          USING EVENTS: AND(around(abs, 158, 20)) FOR [1,] hours AS tunnel
          YIELD: all periods
          """;

      var result = queryService.query(dps, query);

      assertThat(result.type()).isEqualTo(QueryResultType.PERIOD_SET);
      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlPeriodSet.class))
          .satisfies(periodSet -> {
            assertThat(periodSet.totalPeriods()).isEqualTo(2);
            assertThat(periodSet.isEmpty()).isFalse();
            assertThat(periodSet.periods())
                .elements(0, 1)
                .containsExactly(
                    QueryResult.of(0, Instant.parse("2022-08-09T07:52:27.627Z"), Instant.parse("2022-08-09T20:22:27.627Z")),
                    QueryResult.of(1, Instant.parse("2022-08-10T10:52:27.627Z"), Instant.parse("2022-08-10T16:22:27.627Z"))
                );
          });
    }

    @ParameterizedTest
    @TsdlTestSources(
        @TsdlTestSource(value = DATA_ROOT + "series11.csv", skipHeaders = 5)
    )
    @TsdlTestVisualization(renderPointShape = false)
    void queryEvent_aroundRelativeWithSample_detectsPeriods(List<DataPoint> dps) {
      // [155.83, 181.84]
      var query = """
          WITH SAMPLES: avg() AS myAVg, stddev("2022-08-10T10:52:27.627Z", "2022-08-10T16:22:27.627Z") AS myStdDev
          USING EVENTS: AND(around(rel, myAVg, myStdDev)) FOR [1,] hours AS tunnel
          YIELD: all periods
          """;

      var result = queryService.query(dps, query);

      assertThat(result.type()).isEqualTo(QueryResultType.PERIOD_SET);
      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlPeriodSet.class))
          .satisfies(periodSet -> {
            assertThat(periodSet.totalPeriods()).isEqualTo(2);
            assertThat(periodSet.isEmpty()).isFalse();
            assertThat(periodSet.periods())
                .elements(0, 1)
                .containsExactly(
                    QueryResult.of(0, Instant.parse("2022-08-09T14:07:27.627Z"), Instant.parse("2022-08-09T19:52:27.627Z")),
                    QueryResult.of(1, Instant.parse("2022-08-10T11:37:27.627Z"), Instant.parse("2022-08-10T16:22:27.627Z"))
                );
          });
    }

    @ParameterizedTest
    @TsdlTestSources(
        @TsdlTestSource(value = DATA_ROOT + "series12.csv", skipHeaders = 5)
    )
    @TsdlTestVisualization(renderPointShape = false, dateAxisFormat = "dd HH:mm")
    void queryEvent_constantEvent(List<DataPoint> dps) {
      var query = "USING EVENTS: AND(const(20,13.5)) FOR [3,] hours AS myConstEvent YIELD: all periods";
      var queryResult = queryService.query(dps, query);
      for (TsdlPeriod p : ((TsdlPeriodSet) queryResult).periods()) {
        System.out.printf("%s--%s (%s hours)%n", p.start(), p.end(), TsdlUtil.getTimespan(p.start(), p.end(), TsdlTimeUnit.HOURS));
      }

      /* potentials for improvement:
       * find way to set DERIVATIVE_THRESHOLD, maybe dynamically based on data?
       * implement neighbourhood search, i.e., explore left and right extensions/reductions of the heuristic intervals and pick the longest one
       */
    }

    @ParameterizedTest
    @TsdlTestSources(
        @TsdlTestSource(value = DATA_ROOT + "series12.csv", skipHeaders = 5)
    )
    @TsdlTestVisualization(renderPointShape = false, dateAxisFormat = "dd HH:mm")
    void queryEvent_increaseEvent(List<DataPoint> dps) {
      var query = "USING EVENTS: AND(increase(50,-,0.5)) AS myIncreaseEvent YIELD: all periods";
      var queryResult = queryService.query(dps, query);
      for (TsdlPeriod p : ((TsdlPeriodSet) queryResult).periods()) {
        System.out.printf("%s--%s (%s hours)%n", p.start(), p.end(), TsdlUtil.getTimespan(p.start(), p.end(), TsdlTimeUnit.HOURS));
      }

      /* potentials for improvement:
       * the criterion for allowing temporary negative rates of change has as consequence that (near) constant intervals at the start, end or in the
       * middle of an increase/decrease period are also considered an increase/decrease - which should not be the case. one would need to add
       * additional conditions such that temporary negative/near-zero rates are tolerated, but only for a specific (small) amount of time. then, the
       * increase starting at ~06T22:00 would also be detected (it has an increase of > 200 %)
       */
    }

    @ParameterizedTest
    @TsdlTestSources(
        @TsdlTestSource(value = DATA_ROOT + "series12.csv", skipHeaders = 5)
    )
    @TsdlTestVisualization(renderPointShape = false, dateAxisFormat = "dd HH:mm")
    void queryEvent_decreaseEvent(List<DataPoint> dps) {
      var query = "USING EVENTS: AND(decrease(50,-,0.5)) AS myIncreaseEvent YIELD: all periods";
      var queryResult = queryService.query(dps, query);
      for (TsdlPeriod p : ((TsdlPeriodSet) queryResult).periods()) {
        System.out.printf("%s--%s (%s hours)%n", p.start(), p.end(), TsdlUtil.getTimespan(p.start(), p.end(), TsdlTimeUnit.HOURS));
      }

      /*
       * potentials for improvement:
       * see test 'queryEvent_increaseEvent' with inverse effect that the incorrect (false positive) "decrase" from ~07T02:45 until ~08T07:45 would
       * not be detected, but only the correct decrease from ~07T20:00 until ~08T06:00
       */
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
      var query = "USING EVENTS:\nAND(lt(80)) AS low,\nOR(gt(80.0)) AS high\nCHOOSE:\n(low precedes high)\nYIELD:\nall periods";

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
    @TsdlTestVisualization(skipVisualization = true)
    void queryChooseEvents_lowPrecedesHighLiteralEventDefinitionWithTimeTolerance_detectsPeriod(List<DataPoint> dps) {
      var query = "USING EVENTS:\nAND(lt(80)) AS low,\nOR(gt(80.0)) AS high\nCHOOSE:\n(low precedes high WITHIN [0,15] minutes)\nYIELD:\nall periods";

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
    void queryChooseEvents_lowPrecedesHighLiteralEventDefinitionWithTooLongGap_doesNotDetectPeriod(List<DataPoint> dps) {
      var query = "USING EVENTS:\nAND(lt(80)) AS low,\nOR(gt(80.0)) AS high\nCHOOSE:\n(low precedes high WITHIN [0,15) minutes)\nYIELD:\nall periods";

      var result = queryService.query(dps, query);

      assertThat(result.type()).isEqualTo(QueryResultType.PERIOD_SET);
      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlPeriodSet.class))
          .satisfies(periodSet -> {
            assertThat(periodSet.totalPeriods()).isZero();
            assertThat(periodSet.periods()).isEmpty();
            assertThat(periodSet.isEmpty()).isTrue();
          });
    }

    @ParameterizedTest
    @TsdlTestSources({
        @TsdlTestSource(value = DATA_ROOT + "series2.csv")
    })
    void queryChooseEvents_lowPrecedesHighLiteralEventDefinitionWithTooShortGap_doesNotDetectPeriod(List<DataPoint> dps) {
      var query = "USING EVENTS:\nAND(lt(80)) AS low,\nOR(gt(80.0)) AS high\nCHOOSE:\n(low precedes high WITHIN (15,] minutes)\nYIELD:\nall periods";

      var result = queryService.query(dps, query);

      assertThat(result.type()).isEqualTo(QueryResultType.PERIOD_SET);
      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlPeriodSet.class))
          .satisfies(periodSet -> {
            assertThat(periodSet.totalPeriods()).isZero();
            assertThat(periodSet.periods()).isEmpty();
            assertThat(periodSet.isEmpty()).isTrue();
          });
    }

    @ParameterizedTest
    @TsdlTestSources({
        @TsdlTestSource(value = DATA_ROOT + "series2_gap.csv")
    })
    void queryChooseEvents_lowPrecedesHighLiteralEventDefinitionWithGap_doesNotDetectPeriod(List<DataPoint> dps) {
      // gap is constituted by data point with value of exactly 80.0 right between "low" and "high"
      var query = "USING EVENTS:\nAND(lt(80)) AS low,\nOR(gt(80.0)) AS high\nCHOOSE:\n(low precedes high)\nYIELD:\nall periods";

      var result = queryService.query(dps, query);

      assertThat(result.type()).isEqualTo(QueryResultType.PERIOD_SET);
      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlPeriodSet.class))
          .satisfies(periodSet -> {
            assertThat(periodSet.totalPeriods()).isZero();
            assertThat(periodSet.periods()).isEmpty();
            assertThat(periodSet.isEmpty()).isTrue();
          });
    }

    @ParameterizedTest
    @TsdlTestSources({
        @TsdlTestSource(value = DATA_ROOT + "series2_gap.csv")
    })
    void queryChooseEvents_lowPrecedesHighLiteralEventDefinitionWithGapAndTimeTolerance_detectsPeriod(List<DataPoint> dps) {
      // gap is constituted by data point with value of exactly 80.0 right between "low" and "high"
      var query = "USING EVENTS:\nAND(lt(80)) AS low,\nOR(gt(80.0)) AS high\nCHOOSE:\n(low precedes high WITHIN [,15] minutes)\nYIELD:\nall periods";

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
    void queryChooseEvents_highFollowsLowLiteralEventDefinition_detectsPeriod(List<DataPoint> dps) {
      var query = "USING EVENTS:\nAND(lt(80)) AS low,\nOR(gt(80.0)) AS high\nCHOOSE:\n(high follows low)\nYIELD:\nall periods";

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
      var query = "USING EVENTS:\nAND(lt(80)) AS low,\nOR(gt(80.0)) AS high\nCHOOSE:\n(high follows low)\nYIELD:\nall periods";

      var result = queryService.query(dps, query);

      assertThat(result.type()).isEqualTo(QueryResultType.PERIOD_SET);
      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlPeriodSet.class))
          .satisfies(periodSet -> {
            assertThat(periodSet.totalPeriods()).isZero();
            assertThat(periodSet.periods()).isEmpty();
            assertThat(periodSet.isEmpty()).isTrue();
          });
    }

    @ParameterizedTest
    @TsdlTestSources({
        @TsdlTestSource(value = DATA_ROOT + "series2_gap.csv")
    })
    void queryChooseEvents_lowPrecedesHighLiteralEventDefinitionWithGapAndUniversalTimeTolerance_detectsPeriod(List<DataPoint> dps) {
      // gap is constituted by data point with value of exactly 80.0 right between "low" and "high"
      var query = "USING EVENTS:\nAND(lt(80)) AS low,\nOR(gt(80.0)) AS high\nCHOOSE:\n(low precedes high WITHIN [,] seconds)\nYIELD:\nall periods";

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
                      avg() AS myAvg
                    USING EVENTS:
                      AND(lt(myAvg)) AS low,
                      AND(gt(myAvg)) AS high
                    CHOOSE:
                      (low follows high)
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
                      (low follows high)
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
    void queryChooseEvents_lowFollowsHighSampleEventDefinitionWithDurationConstraintAndTimeTolerance_detectsReducedNumberOfResultPeriods(
        List<DataPoint> dps) {
      var query = """
          WITH SAMPLES:
                      avg() AS myAvg
                    USING EVENTS:
                      AND(lt(myAvg)) AS low,
                      AND(gt(myAvg)) FOR [2,] hours AS high
                    CHOOSE:
                      (low follows high WITHIN [0,900000] millis)
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
    void queryChooseEvents_lowFollowsHighSampleEventDefinitionWithDurationConstraintAndTooBigTimeGap_detectsNoPeriod(
        List<DataPoint> dps) {
      var query = """
          WITH SAMPLES:
                      avg() AS myAvg
                    USING EVENTS:
                      AND(lt(myAvg)) AS low,
                      AND(gt(myAvg)) FOR [2,] hours AS high
                    CHOOSE:
                      (low follows high WITHIN [0,900000) millis)
                    YIELD:
                      all periods""";

      var result = queryService.query(dps, query);

      assertThat(result.type()).isEqualTo(QueryResultType.PERIOD_SET);
      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlPeriodSet.class))
          .satisfies(periodSet -> {
            assertThat(periodSet.totalPeriods()).isZero();
            assertThat(periodSet.periods()).isEmpty();
            assertThat(periodSet.isEmpty()).isTrue();
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
                      (low follows high)
                    YIELD:
                      all periods""";

      var result = queryService.query(dps, query);

      assertThat(result.type()).isEqualTo(QueryResultType.PERIOD_SET);
      assertThat(result)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlPeriodSet.class))
          .satisfies(periodSet -> {
            assertThat(periodSet.totalPeriods()).isZero();
            assertThat(periodSet.periods()).isEmpty();
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
                      (low precedes high)
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
                      (low follows high)
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
                      (high precedes low)
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
                      (high precedes low)
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
                      (high precedes low)
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
                      (high precedes low)
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
                      (high precedes low)
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
                      (high precedes low)
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

    @ParameterizedTest
    @TsdlTestSources({
        @TsdlTestSource(value = DATA_ROOT + "series13.csv", skipHeaders = 5)
    })
    @TsdlTestVisualization(renderPointShape = false)
    void queryChooseEvents_choiceWithOneRecursiveOperand(List<DataPoint> dps) {
      var query = """
          USING EVENTS:
            AND(gt(60), lt(120)) AS low,
            AND(NOT(lt(120)), lt(250)) AS med,
            AND(NOT(lt(250))) AS high
          CHOOSE:
            (low precedes (med precedes high))
          YIELD:
            all periods
          """;

      var queryResult = queryService.query(dps, query);
      System.out.println();
    }

    @ParameterizedTest
    @TsdlTestSources({
        @TsdlTestSource(value = DATA_ROOT + "series13.csv", skipHeaders = 5)
    })
    @TsdlTestVisualization(renderPointShape = false)
    void queryChooseEvents_choiceWithTwoRecursiveOperands(List<DataPoint> dps) {
      var query = """
          USING EVENTS:
            AND(gt(60), lt(120)) AS low,
            AND(NOT(lt(120)), lt(250)) AS med,
            AND(NOT(lt(250))) AS high
          CHOOSE:
            ((low follows med) precedes (med precedes high))
          YIELD:
            all periods
          """;

      var queryResult = queryService.query(dps, query);
      System.out.println();
    }

    @ParameterizedTest
    @TsdlTestSources({
        @TsdlTestSource(value = DATA_ROOT + "series13.csv", skipHeaders = 5)
    })
    @TsdlTestVisualization(renderPointShape = false)
    void queryChooseEvents_choiceWithTwoRecursiveOperandsOneDoublyRecursive(List<DataPoint> dps) {
      var query = """
          USING EVENTS:
            AND(gt(60), lt(120)) AS low,
            AND(NOT(lt(120)), lt(250)) AS med,
            AND(NOT(lt(250))) AS high
          CHOOSE:
            ((low precedes (low follows med)) precedes (med precedes high))
          YIELD:
            all periods
          """;

      var queryResult = queryService.query(dps, query);
      System.out.println();
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

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.QueryServiceDataFactory#dataPoints_1")
    void queryFilter_aroundAbsoluteFilter(List<DataPoint> dataPoints) {
      filterTest("APPLY FILTER: AND(around(abs, 50.1, 24.35)) YIELD: data points", dataPoints,
          List.of(dataPoints.get(0), dataPoints.get(1), dataPoints.get(4), dataPoints.get(5)));
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.QueryServiceDataFactory#dataPoints_1")
    void queryFilter_negatedAroundAbsoluteFilter(List<DataPoint> dataPoints) {
      filterTest("APPLY FILTER: AND(NOT(around(abs, 50.1, 24.35))) YIELD: data points", dataPoints,
          List.of(dataPoints.get(2), dataPoints.get(3)));
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.QueryServiceDataFactory#dataPoints_1")
    void queryFilter_aroundRelativeFilter(List<DataPoint> dataPoints) {
      filterTest("APPLY FILTER: AND(around(rel, 50.1, 50.8)) YIELD: data points", dataPoints,
          List.of(dataPoints.get(0), dataPoints.get(1), dataPoints.get(2), dataPoints.get(4), dataPoints.get(5)));
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.QueryServiceDataFactory#dataPoints_1")
    void queryFilter_negatedAroundRelativeFilter(List<DataPoint> dataPoints) {
      filterTest("APPLY FILTER: AND(NOT(around(rel, 50.1, 50.8))) YIELD: data points", dataPoints,
          List.of(dataPoints.get(3)));
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.QueryServiceDataFactory#dataPoints_1")
    void queryFilter_aroundAbsoluteFilterWithSampleArgument(List<DataPoint> dataPoints) {
      filterTest("WITH SAMPLES: avg() AS s APPLY FILTER: AND(around(abs, 50.1, s)) YIELD: data points", dataPoints,
          List.of(dataPoints.get(0), dataPoints.get(1), dataPoints.get(2), dataPoints.get(3), dataPoints.get(4), dataPoints.get(5)));
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.QueryServiceDataFactory#dataPoints_1")
    void queryFilter_negatedAroundAbsoluteFilterWithSampleArgument(List<DataPoint> dataPoints) {
      filterTest("WITH SAMPLES: avg() AS s APPLY FILTER: AND(NOT(around(abs, 50.1, s))) YIELD: data points", dataPoints,
          List.of());
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.QueryServiceDataFactory#dataPoints_1")
    void queryFilter_aroundRelativeFilterWithSampleArgument(List<DataPoint> dataPoints) {
      filterTest("WITH SAMPLES: stddev() AS s APPLY FILTER: AND(around(rel, 50.1, s)) YIELD: data points", dataPoints,
          List.of(dataPoints.get(4)));
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.QueryServiceDataFactory#dataPoints_1")
    void queryFilter_negatedAroundRelativeFilterWithSampleArgument(List<DataPoint> dataPoints) {
      filterTest("WITH SAMPLES: stddev() AS s APPLY FILTER: AND(NOT(around(rel, 50.1, s))) YIELD: data points", dataPoints,
          List.of(dataPoints.get(0), dataPoints.get(1), dataPoints.get(2), dataPoints.get(3), dataPoints.get(5)));
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
                    CHOOSE: (low follows high)
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
    void queryIntegration_queryWithSampleEchos_collectsLogMessages(String yield) {
      var query = """
          WITH SAMPLES: avg() AS m1 -> echo(3), max() AS m2 -> echo(0)
                    USING EVENTS: AND(lt(m1)) AS low, OR(gt(m1)) AS high
                    CHOOSE: (low precedes high)
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
              "sample 'm1' avg() := 2.000",
              "sample 'm2' max() := 3"
          ));
    }

    @ParameterizedTest
    @ValueSource(strings = {"data points", "all periods", "shortest period", "longest period", "sample m1", "sample m2", "samples m1", "samples m2",
        "samples m1, m2", "samples m2,m1"})
    void queryIntegration_queryWithLocalAndTemporalSampleEchos_collectsLogMessages(String yield) {
      var query = """
          WITH SAMPLES: avg("2022-07-31T08:12:59.123Z","") AS m1 -> echo(3), max("","2022-07-31T10:12:59.123Z") AS m2 -> echo(0),
                        avg_t(days, "2022-07-31T08:12:59.123Z/2022-07-31T09:12:59.123Z") AS m3 -> echo(3),
                        count_t("2022-07-31T08:12:59.123Z/2022-07-31T09:12:59.123Z") AS m4 -> echo(0)
                    USING EVENTS: AND(lt(m1)) AS low, OR(gt(m1)) AS high
                    CHOOSE: (low precedes high)
                    YIELD: %s""".formatted(yield);

      var input = List.of(
          DataPoint.of(Instant.parse("2022-07-31T08:12:59.123Z"), 1.),
          DataPoint.of(Instant.parse("2022-07-31T09:12:59.123Z"), 2.),
          DataPoint.of(Instant.parse("2022-07-31T10:12:59.123Z"), 3.)
      );
      var result = queryService.query(input, query);

      assertThat(result.logs())
          .hasSize(4)
          .extracting(TsdlLogEvent::message)
          .isEqualTo(List.of(
              "sample 'm1' avg(\"2022-07-31T08:12:59.123Z\", \"\") := 2.000",
              "sample 'm2' max(\"\", \"2022-07-31T10:12:59.123Z\") := 3",
              "sample 'm3' avg_t(days, \"2022-07-31T08:12:59.123Z/2022-07-31T09:12:59.123Z\") := 0.042 days",
              "sample 'm4' count_t(\"2022-07-31T08:12:59.123Z/2022-07-31T09:12:59.123Z\") := 1"
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
