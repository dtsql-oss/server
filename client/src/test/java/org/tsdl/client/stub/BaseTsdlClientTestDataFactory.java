package org.tsdl.client.stub;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import org.tsdl.infrastructure.dto.QueryResultDto;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.infrastructure.model.TsdlLogEvent;

@SuppressWarnings("unused") // only referenced by string literals, therefore usage unrecognized
public final class BaseTsdlClientTestDataFactory {
  private BaseTsdlClientTestDataFactory() {
  }

  public static Stream<Arguments> query_serviceReturnsDataPoints_deserializesCorrectly() {
    return Stream.of(
        Arguments.of(
            """
                {
                "result" : {
                "items" : [ {
                "timestamp" : "2022-12-15T01:21:48Z",
                "value" : "37.0"
                }, {
                "timestamp" : "2022-12-15T01:36:48Z",
                "value" : "41.0"
                }, {
                "timestamp" : "2022-12-15T01:51:48Z",
                "value" : "45.0"
                } ],
                "logs" : [ {
                "dateTime" : "2022-06-26T13:12:22.067587Z",
                "message" : "sample 'mean1' of 'avg' aggregator := 151.030"
                }, {
                "dateTime" : "2022-06-26T13:12:22.068586400Z",
                "message" : "sample 'max1' of 'max' aggregator := 335.0"
                } ]
                },
                "type" : "DATA_POINTS"
                }""",
            dto(QueryResult.of(
                List.of(
                    dp("2022-12-15T01:21:48Z", 37.0),
                    dp("2022-12-15T01:36:48Z", 41.0),
                    dp("2022-12-15T01:51:48Z", 45.0)
                ),
                ev("2022-06-26T13:12:22.067587Z", "sample 'mean1' of 'avg' aggregator := 151.030"),
                ev("2022-06-26T13:12:22.068586400Z", "sample 'max1' of 'max' aggregator := 335.0")
            ))
        ),
        Arguments.of(
            """
                {
                "result" : {
                "items" : [ {
                "timestamp" : "2022-12-15T01:21:48Z",
                "value" : "37.0"
                }, {
                "timestamp" : "2022-12-15T01:36:48Z",
                "value" : "41.0"
                }, {
                "timestamp" : "2022-12-15T01:51:48Z",
                "value" : "45.0"
                } ],
                "logs" : [ ]
                },
                "type" : "DATA_POINTS"
                }""",
            dto(QueryResult.of(
                List.of(
                    dp("2022-12-15T01:21:48Z", 37.0),
                    dp("2022-12-15T01:36:48Z", 41.0),
                    dp("2022-12-15T01:51:48Z", 45.0)
                )
            ))
        )
    );
  }

  public static Stream<Arguments> query_serviceReturnsPeriod_deserializesCorrectly() {
    return Stream.of(
        Arguments.of(
            """
                {
                "result" : {
                 "empty" : false,
                 "end" : "2022-12-15T09:21:48Z",
                 "start": "2022-12-15T01:21:48Z",
                 "index" : 0,
                 "logs" : [ ]
                 },
                "type" : "PERIOD"
                }""",
            dto(
                QueryResult.of(0, Instant.parse("2022-12-15T01:21:48Z"), Instant.parse("2022-12-15T09:21:48Z")
                )
            )
        ),
        Arguments.of(
            """
                {
                 "result" : {
                "empty" : false,
                "end" : "2022-12-15T09:21:48Z",
                "start": "2022-12-15T01:21:48Z",
                "index" : 0,
                "logs" : [ {
                "dateTime" : "2022-06-26T14:10:01.117410600Z",
                "message" : "sample 'mean1' of 'avg' aggregator := 151.0"
                } ]
                },
                 "type" : "PERIOD"
                 }""",
            dto(QueryResult.of(0, Instant.parse("2022-12-15T01:21:48Z"), Instant.parse("2022-12-15T09:21:48Z"),
                ev("2022-06-26T14:10:01.117410600Z", "sample 'mean1' of 'avg' aggregator := 151.0")
            ))
        )
    );
  }

  public static Stream<Arguments> query_serviceReturnsPeriodSet_deserializesCorrectly() {
    return Stream.of(
        Arguments.of(
            """
                {
                "result" : {
                "empty" : false,
                "logs" : [ ],
                "periods" : [ {
                "empty" : false,
                "end" : "2022-12-15T09:21:48Z",
                "logs": [],
                "index" : 0,
                "start" : "2022-12-15T01:21:48Z"
                } ],
                "totalPeriods" : 1
                },
                "type" : "PERIOD_SET"
                }""",
            dto(QueryResult.of(
                1,
                List.of(
                    QueryResult.of(0, Instant.parse("2022-12-15T01:21:48Z"), Instant.parse("2022-12-15T09:21:48Z")
                    ))
            ))
        ),
        Arguments.of(
            """
                {
                 "result" : {
                 "empty" : false,
                 "logs" : [ {
                 "dateTime" : "2022-06-26T14:01:55.525990200Z",
                 "message" : "sample 'mean1' of 'avg' aggregator := 151.0"
                 } ],
                 "periods" : [ {
                 "empty" : false,
                 "end" : "2022-12-15T09:21:48Z",
                 "index" : 0,
                 "logs" : [ ],
                 "start" : "2022-12-15T01:21:48Z"
                 } ],
                 "totalPeriods" : 1
                 },
                 "type" : "PERIOD_SET"
                 }""",
            dto(QueryResult.of(
                1,
                List.of(
                    QueryResult.of(0, Instant.parse("2022-12-15T01:21:48Z"), Instant.parse("2022-12-15T09:21:48Z")
                    )),
                ev("2022-06-26T14:01:55.525990200Z", "sample 'mean1' of 'avg' aggregator := 151.0")
            ))
        )
    );
  }

  public static Stream<Arguments> query_serviceReturnsScalar_deserializesCorrectly() {
    return Stream.of(
        Arguments.of(
            """
                {
                "result" : {
                "logs" : [ ],
                "value" : 151.03030303030303
                },
                "type" : "SCALAR"
                }""",
            dto(QueryResult.of(151.03030303030303))
        ),
        Arguments.of(
            """
                {
                 "result" : {
                "logs" : [ {
                "dateTime" : "2022-06-26T14:14:11.548995900Z",
                "message" : "sample 'mean1' of 'avg' aggregator := 151.0"
                } ],
                "value" : 151.03030303030303
                },
                 "type" : "SCALAR"
                 }""",
            dto(QueryResult.of(151.03030303030303,
                ev("2022-06-26T14:14:11.548995900Z", "sample 'mean1' of 'avg' aggregator := 151.0")
            ))
        )
    );
  }

  public static Stream<Arguments> query_serviceReturnsScalarList_deserializesCorrectly() {
    return Stream.of(
        Arguments.of(
            """
                {
                "result" : {
                 "logs" : [ ],
                 "values" : [ 151.03030303030303, -77.0 ]
                 },
                "type": "SCALAR_LIST"
                }""",
            dto(QueryResult.of(new Double[] {151.03030303030303, -77.0}))
        ),
        Arguments.of(
            """
                {
                 "result" : {
                 "logs" : [ {
                 "dateTime" : "2022-06-26T14:19:25.731197800Z",
                 "message" : "sample 'mean1' of 'avg' aggregator := 151.0"
                 } ],
                 "values" : [ 151.03030303030303, -77.0 ]
                 },
                 "type" : "SCALAR_LIST"
                 }""",
            dto(QueryResult.of(new Double[] {151.03030303030303, -77.},
                ev("2022-06-26T14:19:25.731197800Z", "sample 'mean1' of 'avg' aggregator := 151.0")
            ))
        )
    );
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
