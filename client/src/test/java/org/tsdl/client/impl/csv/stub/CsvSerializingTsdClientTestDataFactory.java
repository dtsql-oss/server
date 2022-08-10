package org.tsdl.client.impl.csv.stub;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import org.tsdl.client.impl.csv.CsvSerializingQueryClientSpecification;
import org.tsdl.infrastructure.dto.QueryResultDto;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.infrastructure.model.QueryResult;

@SuppressWarnings("unused") // only referenced by string literals, therefore usage unrecognized
public final class CsvSerializingTsdClientTestDataFactory {
  private CsvSerializingTsdClientTestDataFactory() {
  }

  public static Stream<Arguments> query_serviceReturnsDataPoints_writesFileCorrectly() {
    return Stream.of(
        Arguments.of(spec(), tempFile(), dto(QueryResult.of(List.of()))),
        Arguments.of(spec(), tempFile(), dto(QueryResult.of(List.of())))
    );
  }

  public static Stream<Arguments> query_serviceReturnsPeriod_writesFileCorrectly() {
    return Stream.of(
        Arguments.of(
            spec(),
            tempFile(),
            dto(QueryResult.of(0, Instant.parse("2022-12-15T01:21:48Z"), Instant.parse("2022-12-15T09:21:48Z")))
        ),
        Arguments.of(
            spec(),
            tempFile(),
            dto(QueryResult.of(0, Instant.parse("2022-12-15T01:21:48Z"), Instant.parse("2022-12-15T09:21:48Z")))
        )
    );
  }

  public static Stream<Arguments> query_serviceReturnsPeriodSet_writesFileCorrectly() {
    return Stream.of(
        Arguments.of(
            spec(),
            tempFile(),
            dto(QueryResult.of(1, List.of(QueryResult.of(0, Instant.parse("2022-12-15T01:21:48Z"), Instant.parse("2022-12-15T09:21:48Z")))))
        ),
        Arguments.of(
            spec(),
            tempFile(),
            dto(QueryResult.of(1, List.of(QueryResult.of(0, Instant.parse("2022-12-15T01:21:48Z"), Instant.parse("2022-12-15T09:21:48Z")))))
        )
    );
  }

  public static Stream<Arguments> query_serviceReturnsScalar_writesFileCorrectly() {
    return Stream.of(
        Arguments.of(
            spec(),
            tempFile(),
            dto(QueryResult.of(151.03030303030303))
        ),
        Arguments.of(
            spec(),
            tempFile(),
            dto(QueryResult.of(151.03030303030303)))
    );
  }

  public static Stream<Arguments> query_serviceReturnsScalarList_writesFileCorrectly() {
    return Stream.of(
        Arguments.of(
            spec(),
            tempFile(),
            dto(QueryResult.of(new Double[] {151.03030303030303, -77.0}))
        ),
        Arguments.of(
            spec(),
            tempFile(),
            dto(QueryResult.of(new Double[] {151.03030303030303, -77.}))
        )
    );
  }

  private static CsvSerializingQueryClientSpecification spec() {
    return new CsvSerializingQueryClientSpecification(null, "");
  }

  private static String tempFile() {
    return Path.of(System.getProperty("java.io.tmpdir"), "CsvWriterTest_" + UUID.randomUUID() + ".csv").toString();
  }

  private static QueryResultDto dto(QueryResult result) {
    return new QueryResultDto(result, result.type());
  }

  private static DataPoint dp(String date, Double val) {
    return DataPoint.of(Instant.parse(date), val);
  }
}
