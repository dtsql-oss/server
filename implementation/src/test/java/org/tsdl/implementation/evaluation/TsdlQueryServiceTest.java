package org.tsdl.implementation.evaluation;

import org.junit.jupiter.api.Test;
import org.tsdl.infrastructure.api.QueryService;
import org.tsdl.infrastructure.model.DataPoint;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TsdlQueryServiceTest {
    private static final QueryService queryService = new TsdlQueryService();

    @Test
    void query_lt() {
        var query = """
          operator=lt
          threshold=27.25
          """;

        var dp1 = DataPoint.of(Instant.now().minus(Duration.of(5, ChronoUnit.SECONDS)), 25.75);
        var dp2 = DataPoint.of(Instant.now().minus(Duration.of(3, ChronoUnit.SECONDS)), 27.25);
        var dp3 = DataPoint.of(Instant.now().minus(Duration.of(1, ChronoUnit.SECONDS)), 75.25);

        var input = List.of(dp1, dp2, dp3);
        var expectedItems = List.of(dp1);

        var result = queryService.query(input, query);

        assertThat(result.getItems()).usingRecursiveComparison().isEqualTo(expectedItems);
    }

    @Test
    void query_gt() {
        var query = """
          operator=gt
          threshold=700
          """;

        var dp1 = DataPoint.of(Instant.now().minus(Duration.of(5, ChronoUnit.SECONDS)), 25.75);
        var dp2 = DataPoint.of(Instant.now().minus(Duration.of(3, ChronoUnit.SECONDS)), 27.25);
        var dp3 = DataPoint.of(Instant.now().minus(Duration.of(1, ChronoUnit.SECONDS)), 75.25);

        var input = List.of(dp1, dp2, dp3);
        var expectedItems = List.of();

        var result = queryService.query(input, query);

        assertThat(result.getItems()).usingRecursiveComparison().isEqualTo(expectedItems);
    }
}
