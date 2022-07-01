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
            "{\n"
                + "\"result\" : {\n"
                + "\"items\" : [ {\n"
                + "\"timestamp\" : \"2022-12-15T01:21:48Z\",\n"
                + "\"value\" : \"37.0\"\n"
                + "}, {\n"
                + "\"timestamp\" : \"2022-12-15T01:36:48Z\",\n"
                + "\"value\" : \"41.0\"\n"
                + "}, {\n"
                + "\"timestamp\" : \"2022-12-15T01:51:48Z\",\n"
                + "\"value\" : \"45.0\"\n"
                + "} ],\n"
                + "\"logs\" : [ {\n"
                + "\"dateTime\" : \"2022-06-26T13:12:22.067587Z\",\n"
                + "\"message\" : \"sample 'mean1' of 'avg' aggregator := 151.030\"\n"
                + "}, {\n"
                + "\"dateTime\" : \"2022-06-26T13:12:22.068586400Z\",\n"
                + "\"message\" : \"sample 'max1' of 'max' aggregator := 335.0\"\n"
                + "} ]\n"
                + "},\n"
                + "\"type\" : \"DATA_POINTS\"\n"
                + "}",
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
            "{\n"
                + "\"result\" : {\n"
                + "\"items\" : [ {\n"
                + "\"timestamp\" : \"2022-12-15T01:21:48Z\",\n"
                + "\"value\" : \"37.0\"\n"
                + "}, {\n"
                + "\"timestamp\" : \"2022-12-15T01:36:48Z\",\n"
                + "\"value\" : \"41.0\"\n"
                + "}, {\n"
                + "\"timestamp\" : \"2022-12-15T01:51:48Z\",\n"
                + "\"value\" : \"45.0\"\n"
                + "} ],\n"
                + "\"logs\" : [ ]\n"
                + "},\n"
                + "\"type\" : \"DATA_POINTS\"\n"
                + "}",
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
            "{\n"
                + "\"result\" : {\n"
                + " \"empty\" : false,\n"
                + " \"end\" : \"2022-12-15T09:21:48Z\",\n"
                + " \"start\": \"2022-12-15T01:21:48Z\",\n"
                + " \"index\" : 0,\n"
                + " \"logs\" : [ ]\n"
                + " },\n"
                + "\"type\" : \"PERIOD\"\n"
                + "}",
            dto(
                QueryResult.of(0, Instant.parse("2022-12-15T01:21:48Z"), Instant.parse("2022-12-15T09:21:48Z")
                )
            )
        ),
        Arguments.of(
            "{\n"
                + " \"result\" : {\n"
                + "\"empty\" : false,\n"
                + "\"end\" : \"2022-12-15T09:21:48Z\",\n"
                + "\"start\": \"2022-12-15T01:21:48Z\",\n"
                + "\"index\" : 0,\n"
                + "\"logs\" : [ {\n"
                + "\"dateTime\" : \"2022-06-26T14:10:01.117410600Z\",\n"
                + "\"message\" : \"sample 'mean1' of 'avg' aggregator := 151.0\"\n"
                + "} ]\n"
                + "},\n"
                + " \"type\" : \"PERIOD\"\n"
                + " }",
            dto(QueryResult.of(0, Instant.parse("2022-12-15T01:21:48Z"), Instant.parse("2022-12-15T09:21:48Z"),
                ev("2022-06-26T14:10:01.117410600Z", "sample 'mean1' of 'avg' aggregator := 151.0")
            ))
        )
    );
  }

  public static Stream<Arguments> query_serviceReturnsPeriodSet_deserializesCorrectly() {
    return Stream.of(
        Arguments.of(
            "{\n"
                + "\"result\" : {\n"
                + "\"empty\" : false,\n"
                + "\"logs\" : [ ],\n"
                + "\"periods\" : [ {\n"
                + "\"empty\" : false,\n"
                + "\"end\" : \"2022-12-15T09:21:48Z\",\n"
                + "\"logs\": [],\n"
                + "\"index\" : 0,\n"
                + "\"start\" : \"2022-12-15T01:21:48Z\"\n"
                + "} ],\n"
                + "\"totalPeriods\" : 1\n"
                + "},\n"
                + "\"type\" : \"PERIOD_SET\"\n"
                + "}",
            dto(QueryResult.of(
                1,
                List.of(
                    QueryResult.of(0, Instant.parse("2022-12-15T01:21:48Z"), Instant.parse("2022-12-15T09:21:48Z")
                    ))
            ))
        ),
        Arguments.of(
            "{\n"
                + " \"result\" : {\n"
                + " \"empty\" : false,\n"
                + " \"logs\" : [ {\n"
                + " \"dateTime\" : \"2022-06-26T14:01:55.525990200Z\",\n"
                + " \"message\" : \"sample 'mean1' of 'avg' aggregator := 151.0\"\n"
                + " } ],\n"
                + " \"periods\" : [ {\n"
                + " \"empty\" : false,\n"
                + " \"end\" : \"2022-12-15T09:21:48Z\",\n"
                + " \"index\" : 0,\n"
                + " \"logs\" : [ ],\n"
                + " \"start\" : \"2022-12-15T01:21:48Z\"\n"
                + " } ],\n"
                + " \"totalPeriods\" : 1\n"
                + " },\n"
                + " \"type\" : \"PERIOD_SET\"\n"
                + " }",
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
            "{\n"
                + "\"result\" : {\n"
                + "\"logs\" : [ ],\n"
                + "\"value\" : 151.03030303030303\n"
                + "},\n"
                + "\"type\" : \"SCALAR\"\n"
                + "}",
            dto(QueryResult.of(151.03030303030303))
        ),
        Arguments.of(
            "{\n"
                + " \"result\" : {\n"
                + "\"logs\" : [ {\n"
                + "\"dateTime\" : \"2022-06-26T14:14:11.548995900Z\",\n"
                + "\"message\" : \"sample 'mean1' of 'avg' aggregator := 151.0\"\n"
                + "} ],\n"
                + "\"value\" : 151.03030303030303\n"
                + "},\n"
                + " \"type\" : \"SCALAR\"\n"
                + " }",
            dto(QueryResult.of(151.03030303030303,
                ev("2022-06-26T14:14:11.548995900Z", "sample 'mean1' of 'avg' aggregator := 151.0")
            ))
        )
    );
  }

  public static Stream<Arguments> query_serviceReturnsScalarList_deserializesCorrectly() {
    return Stream.of(
        Arguments.of(
            "{\n"
                + "\"result\" : {\n"
                + " \"logs\" : [ ],\n"
                + " \"values\" : [ 151.03030303030303, -77.0 ]\n"
                + " },\n"
                + "\"type\": \"SCALAR_LIST\"\n"
                + "}",
            dto(QueryResult.of(new Double[] {151.03030303030303, -77.0}))
        ),
        Arguments.of(
            "{\n"
                + " \"result\" : {\n"
                + " \"logs\" : [ {\n"
                + " \"dateTime\" : \"2022-06-26T14:19:25.731197800Z\",\n"
                + " \"message\" : \"sample 'mean1' of 'avg' aggregator := 151.0\"\n"
                + " } ],\n"
                + " \"values\" : [ 151.03030303030303, -77.0 ]\n"
                + " },\n"
                + " \"type\" : \"SCALAR_LIST\"\n"
                + " }",
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
