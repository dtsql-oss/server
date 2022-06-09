package org.tsdl.implementation.evaluation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.tsdl.infrastructure.api.QueryService;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.infrastructure.model.TsdlDataPoints;
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
        @TsdlTestSource(DATA_ROOT + "series0.csv")
    })
    @TsdlTestVisualization(renderPointShape = false)
    void queryChooseEvents_ltThenGtLiteralArguments(List<DataPoint> dps) {
      var query = """
          USING EVENTS:
            AND(lt(3.4)) AS low,
            OR(gt(3.4)) AS high
                    
          CHOOSE:
            low precedes high
                    
          YIELD:
            all periods
          """;

      // TODO
      //var result = queryService.query(dps, query);
    }

    @ParameterizedTest
    @TsdlTestSources({
        @TsdlTestSource(value = DATA_ROOT + "series2.csv", skipHeaders = 3)
    })
    @TsdlTestVisualization(skipVisualization = true)
    void queryChooseEvents_ltThenGtLiteralArgumentsSmallInput(List<DataPoint> dps) {
      var query = """
          WITH SAMPLES:
            avg(_input) AS mean
            
          FILTER:
            AND(NOT(lt(80)))
            
          USING EVENTS:
            AND(lt(mean)) AS low,
            OR(gt(mean)) AS high

          CHOOSE:
            low precedes high

          YIELD:
            all periods
          """;

      var result = queryService.query(dps, query);
      System.out.println();
    }

    @ParameterizedTest
    @TsdlTestSources({
        @TsdlTestSource(DATA_ROOT + "series1.csv")
    })
    @TsdlTestVisualization(renderPointShape = false)
    void queryChooseEvents_ltThenGtSampleArguments(List<DataPoint> dps) {
      // arithmetic mean of data point values is ~74.16667
      var query = """
          WITH SAMPLES:
            avg(_input) AS mean
                    
          USING EVENTS:
            AND(lt(mean)) AS low, OR(gt(mean)) AS high
                    
          CHOOSE:
            low precedes high
                    
          YIELD:
            all periods
          """;

      // TODO
      //var result = queryService.query(dps, query);
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
    @TsdlTestVisualization(dateAxisFormat = TsdlTestVisualization.PRECISE_AXIS_FORMAT, renderPointShape = true)
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
    @TsdlTestVisualization(dateAxisFormat = TsdlTestVisualization.PRECISE_AXIS_FORMAT, renderPointShape = true)
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
  }
}
