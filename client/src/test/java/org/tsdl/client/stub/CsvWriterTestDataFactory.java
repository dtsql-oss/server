package org.tsdl.client.stub;

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
import org.tsdl.infrastructure.model.TsdlLogEvent;

@SuppressWarnings("unused") // only referenced by string literals, therefore usage unrecognized
public final class CsvWriterTestDataFactory {
  private CsvWriterTestDataFactory() {
  }

  public static Stream<Arguments> query_writeDataPoints_writesFileCorrectly() {
    return Stream.of(
        Arguments.of(
            spec(),
            dto(QueryResult.of(
                List.of(
                    dp("2022-12-15T01:21:48Z", 37.0),
                    dp("2022-12-15T01:36:48Z", 41.0),
                    dp("2022-12-15T01:51:48Z", 45.0)
                ),
                ev("2022-06-26T13:12:22.067587Z", "sample 'mean1' of 'avg' aggregator := 151.030"),
                ev("2022-06-26T13:12:22.068586400Z", "sample 'max1' of 'max' aggregator := 335.0")
            )),
            "#TSDL Query Result\n"
                + "#TYPE=DATA_POINTS\n"
                + "time;value\n"
                + "2022-12-15T01:21:48Z;37.0\n"
                + "2022-12-15T01:36:48Z;41.0\n"
                + "2022-12-15T01:51:48Z;45.0\n"
                + "#TSDL Query Evaluation Logs\n"
                + "timestamp;message\n"
                + "2022-06-26T13:12:22.067587Z;sample 'mean1' of 'avg' aggregator := 151.030\n"
                + "2022-06-26T13:12:22.068586400Z;sample 'max1' of 'max' aggregator := 335.0\n"
        ),
        Arguments.of(
            spec(),
            dto(QueryResult.of(
                List.of(
                    dp("2022-12-15T01:21:48Z", 37.0),
                    dp("2022-12-15T01:36:48Z", 41.0),
                    dp("2022-12-15T01:51:48Z", 45.0)
                )
            )),
            "#TSDL Query Result\n"
                + "#TYPE=DATA_POINTS\n"
                + "time;value\n"
                + "2022-12-15T01:21:48Z;37.0\n"
                + "2022-12-15T01:36:48Z;41.0\n"
                + "2022-12-15T01:51:48Z;45.0\n"
                + "#TSDL Query Evaluation Logs\n"
                + "timestamp;message\n"
        )
    );
  }

  public static Stream<Arguments> query_writePeriod_writesFileCorrectly() {
    return Stream.of(
        Arguments.of(
            spec(),
            dto(
                QueryResult.of(0, Instant.parse("2022-12-15T01:21:48Z"), Instant.parse("2022-12-15T09:21:48Z")
                )
            ),
            "#TSDL Query Result\n"
                + "#TYPE=PERIOD\n"
                + "index;empty;start;end\n"
                + "0;false;2022-12-15T01:21:48Z;2022-12-15T09:21:48Z\n"
                + "#TSDL Query Evaluation Logs\n"
                + "timestamp;message\n"
        ),
        Arguments.of(
            spec(),
            dto(QueryResult.of(0, Instant.parse("2022-12-15T01:21:48Z"), Instant.parse("2022-12-15T09:21:48Z"),
                ev("2022-06-26T14:10:01.117410600Z", "sample 'mean1' of 'avg' aggregator := 151.0")
            )),
            "#TSDL Query Result\n"
                + "#TYPE=PERIOD\n"
                + "index;empty;start;end\n"
                + "0;false;2022-12-15T01:21:48Z;2022-12-15T09:21:48Z\n"
                + "#TSDL Query Evaluation Logs\n"
                + "timestamp;message\n"
                + "2022-06-26T14:10:01.117410600Z;sample 'mean1' of 'avg' aggregator := 151.0\n"
        )
    );
  }

  public static Stream<Arguments> query_writePeriodSet_writesFileCorrectly() {
    return Stream.of(
        Arguments.of(
            spec(),
            dto(QueryResult.of(
                1,
                List.of(
                    QueryResult.of(0, Instant.parse("2022-12-15T01:21:48Z"), Instant.parse("2022-12-15T09:21:48Z")
                    ))
            )),
            "#TSDL Query Result\n"
                + "#TYPE=PERIOD_SET\n"
                + "index;empty;start;end\n"
                + "0;false;2022-12-15T01:21:48Z;2022-12-15T09:21:48Z\n"
                + "#TSDL Query Evaluation Logs\n"
                + "timestamp;message\n"
        ),
        Arguments.of(
            spec(),
            dto(QueryResult.of(
                1,
                List.of(
                    QueryResult.of(0, Instant.parse("2022-12-15T01:21:48Z"), Instant.parse("2022-12-15T09:21:48Z")
                    )),
                ev("2022-06-26T14:01:55.525990200Z", "sample 'mean1' of 'avg' aggregator := 151.0")
            )),
            "#TSDL Query Result\n"
                + "#TYPE=PERIOD_SET\n"
                + "index;empty;start;end\n"
                + "0;false;2022-12-15T01:21:48Z;2022-12-15T09:21:48Z\n"
                + "#TSDL Query Evaluation Logs\n"
                + "timestamp;message\n"
                + "2022-06-26T14:01:55.525990200Z;sample 'mean1' of 'avg' aggregator := 151.0\n"
        )
    );
  }

  public static Stream<Arguments> query_writeScalar_writesFileCorrectly() {
    return Stream.of(
        Arguments.of(
            spec(),
            dto(QueryResult.of(151.03030303030303)),
            "#TSDL Query Result\n"
                + "#TYPE=SCALAR\n"
                + "value\n"
                + "151.03030303030303\n"
                + "#TSDL Query Evaluation Logs\n"
                + "timestamp;message\n"
        ),
        Arguments.of(
            spec(),
            dto(QueryResult.of(151.03030303030303,
                ev("2022-06-26T14:14:11.548995900Z", "sample 'mean1' of 'avg' aggregator := 151.0")
            )),
            "#TSDL Query Result\n"
                + "#TYPE=SCALAR\n"
                + "value\n"
                + "151.03030303030303\n"
                + "#TSDL Query Evaluation Logs\n"
                + "timestamp;message\n"
                + "2022-06-26T14:14:11.548995900Z;sample 'mean1' of 'avg' aggregator := 151.0\n"
        )
    );
  }

  public static Stream<Arguments> query_writeScalarList_writesFileCorrectly() {
    return Stream.of(
        Arguments.of(
            spec(),
            dto(QueryResult.of(new Double[] {151.03030303030303, -77.0})),
            "#TSDL Query Result\n"
                + "#TYPE=SCALAR_LIST\n"
                + "value\n"
                + "151.03030303030303\n"
                + "-77.0\n"
                + "#TSDL Query Evaluation Logs\n"
                + "timestamp;message\n"
        ),
        Arguments.of(
            spec(),
            dto(QueryResult.of(new Double[] {151.03030303030303, -77.},
                ev("2022-06-26T14:19:25.731197800Z", "sample 'mean1' of 'avg' aggregator := 151.0")
            )),
            "#TSDL Query Result\n"
                + "#TYPE=SCALAR_LIST\n"
                + "value\n"
                + "151.03030303030303\n"
                + "-77.0\n"
                + "#TSDL Query Evaluation Logs\n"
                + "timestamp;message\n"
                + "2022-06-26T14:19:25.731197800Z;sample 'mean1' of 'avg' aggregator := 151.0\n"
        )
    );
  }

  private static CsvSerializingQueryClientSpecification spec() {
    return new CsvSerializingQueryClientSpecification(null, "", tempFile());
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

  private static TsdlLogEvent ev(String date, String msg) {
    return TsdlLogEvent.of(Instant.parse(date), msg);
  }
}
