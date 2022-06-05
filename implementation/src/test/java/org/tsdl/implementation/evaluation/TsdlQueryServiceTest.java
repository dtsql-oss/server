package org.tsdl.implementation.evaluation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.tsdl.infrastructure.api.QueryService;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.testutil.creation.provider.TsdlTestSource;
import org.tsdl.testutil.creation.provider.TsdlTestSources;
import org.tsdl.testutil.visualization.api.TsdlTestVisualization;
import org.tsdl.testutil.visualization.impl.TsdlTestVisualizer;

@ExtendWith(TsdlTestVisualizer.class)
class TsdlQueryServiceTest {
  private static final String DATA_ROOT = "data/query/";
  private static final QueryService queryService = new TsdlQueryService();

  @ParameterizedTest
  @TsdlTestSources({
      @TsdlTestSource(DATA_ROOT + "series0.csv")
  })
  @TsdlTestVisualization(renderPointShape = false)
  void query_ltThenGtLiteralArguments(List<DataPoint> dps) {
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
      @TsdlTestSource(DATA_ROOT + "series1.csv")
  })
  @TsdlTestVisualization(renderPointShape = false)
  void query_ltThenGtSampleArguments(List<DataPoint> dps) {
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

  @ParameterizedTest
  @MethodSource("org.tsdl.implementation.evaluation.stub.DataPointDataFactory#dataPoints_0")
  @TsdlTestVisualization(skipVisualization = true)
  void query_lt(List<DataPoint> dataPoints) {
    var query = """
        FILTER:
          AND(lt(27.25))
        YIELD: data points
        """;

    var expectedItems = List.of(dataPoints.get(0));

    var result = queryService.query(dataPoints, query);

    assertThat(result.getItems())
        .usingRecursiveComparison()
        .isEqualTo(expectedItems);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.implementation.evaluation.stub.DataPointDataFactory#dataPoints_0")
  @TsdlTestVisualization(dateAxisFormat = TsdlTestVisualization.PRECISE_AXIS_FORMAT, renderPointShape = true)
  void query_gt(List<DataPoint> dataPoints) {
    var query = """
        FILTER:
          AND(gt(27.24))
        YIELD: data points
        """;

    var expectedItems = List.of(dataPoints.get(1), dataPoints.get(2));

    var result = queryService.query(dataPoints, query);

    assertThat(result.getItems())
        .usingRecursiveComparison()
        .isEqualTo(expectedItems);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.implementation.evaluation.stub.DataPointDataFactory#dataPoints_0")
  @TsdlTestVisualization(skipVisualization = true)
  void query_gtAndLt(List<DataPoint> dataPoints) {
    var query = """
        FILTER:
          AND( gt(25.75), lt(75) )
        YIELD: data points
        """;

    var expectedItems = List.of(dataPoints.get(1));

    var result = queryService.query(dataPoints, query);

    assertThat(result.getItems())
        .usingRecursiveComparison()
        .isEqualTo(expectedItems);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.implementation.evaluation.stub.DataPointDataFactory#dataPoints_0")
  void query_gtOrLt(List<DataPoint> dataPoints) {
    var query = """
        FILTER:
          OR( gt(75), lt(26) )
        YIELD: data points
        """;

    var expectedItems = List.of(dataPoints.get(0), dataPoints.get(2));

    var result = queryService.query(dataPoints, query);

    assertThat(result.getItems())
        .usingRecursiveComparison()
        .isEqualTo(expectedItems);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.implementation.evaluation.stub.DataPointDataFactory#dataPoints_0")
  void query_gtAndNotLt(List<DataPoint> dataPoints) {
    var query = """
        FILTER:
          AND( gt(25.75), NOT(lt(28)) )
        YIELD: data points
        """;

    var expectedItems = List.of(dataPoints.get(2));

    var result = queryService.query(dataPoints, query);

    assertThat(result.getItems())
        .usingRecursiveComparison()
        .isEqualTo(expectedItems);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.implementation.evaluation.stub.DataPointDataFactory#dataPoints_0")
  void query_NotGtOrLt(List<DataPoint> dataPoints) {
    var query = """
        FILTER:
          OR( NOT(gt(27.25)), lt(26) )
        YIELD: data points
        """;

    var expectedItems = List.of(dataPoints.get(0), dataPoints.get(1));

    var result = queryService.query(dataPoints, query);

    assertThat(result.getItems())
        .usingRecursiveComparison()
        .isEqualTo(expectedItems);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.implementation.evaluation.stub.DataPointDataFactory#dataPoints_0")
  void query_impossibleQuery(List<DataPoint> dataPoints) {
    var query = """
        FILTER:
          AND( gt(100), lt(100) )
        YIELD: data points
        """;

    var expectedItems = List.of();

    var result = queryService.query(dataPoints, query);

    assertThat(result.getItems())
        .usingRecursiveComparison()
        .isEqualTo(expectedItems);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.implementation.evaluation.stub.DataPointDataFactory#dataPoints_0")
  void query_trivialQuery(List<DataPoint> dataPoints) {
    var query = """
        FILTER:
          OR( gt(100), lt(100) )
        YIELD: data points
        """;

    var expectedItems = List.of(dataPoints.get(0), dataPoints.get(1), dataPoints.get(2));

    var result = queryService.query(dataPoints, query);

    assertThat(result.getItems())
        .usingRecursiveComparison()
        .isEqualTo(expectedItems);
  }
}
