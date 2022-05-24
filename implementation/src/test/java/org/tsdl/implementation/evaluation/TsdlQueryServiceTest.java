package org.tsdl.implementation.evaluation;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.tsdl.infrastructure.api.QueryService;
import org.tsdl.infrastructure.model.DataPoint;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TsdlQueryServiceTest {
    private static final QueryService queryService = new TsdlQueryService();

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.DataPointDataFactory#dataPoints_0")
    void query_lt(List<DataPoint> dataPoints) {
        var query = """
          FILTER:
            AND(lt(27.25))
          YIELD *
          """;

        var expectedItems = List.of(dataPoints.get(0));

        var result = queryService.query(dataPoints, query);

        assertThat(result.getItems())
          .usingRecursiveComparison()
          .isEqualTo(expectedItems);
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.DataPointDataFactory#dataPoints_0")
    void query_gt(List<DataPoint> dataPoints) {
        var query = """
          FILTER:
            AND(gt(27.24))
          YIELD *
          """;

        var expectedItems = List.of(dataPoints.get(1), dataPoints.get(2));

        var result = queryService.query(dataPoints, query);

        assertThat(result.getItems())
          .usingRecursiveComparison()
          .isEqualTo(expectedItems);
    }

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.evaluation.stub.DataPointDataFactory#dataPoints_0")
    void query_gtAndLt(List<DataPoint> dataPoints) {
        var query = """
          FILTER:
            AND( gt(25.75), lt(75) )
          YIELD *
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
          YIELD *
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
          YIELD *
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
          YIELD *
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
          YIELD *
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
          YIELD *
          """;

        var expectedItems = List.of(dataPoints.get(0), dataPoints.get(1), dataPoints.get(2));

        var result = queryService.query(dataPoints, query);

        assertThat(result.getItems())
          .usingRecursiveComparison()
          .isEqualTo(expectedItems);
    }
}
