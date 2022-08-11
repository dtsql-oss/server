package org.tsdl.implementation.evaluation.stub;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import org.tsdl.infrastructure.model.DataPoint;

@SuppressWarnings("unused") // only referenced by string literals, therefore usage unrecognized
public final class QueryServiceDataFactory {
  private static final DateTimeFormatter INSTANT_FORMATTER = DateTimeFormatter
      .ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
      .withZone(ZoneOffset.UTC);

  private QueryServiceDataFactory() {
  }

  public static Stream<Arguments> dataPoints_0() {
    return Stream.of(
        Arguments.of(
            List.of(
                dp("2022-05-24 20:33:45.000", 25.75),
                dp("2022-05-24 20:33:45.234", 27.25),
                dp("2022-05-24 20:36:44.234", 75.52)
            )
        )
    );
  }

  public static Stream<Arguments> dataPoints_1() {
    return Stream.of(
        Arguments.of(
            List.of(
                dp("2022-05-24 20:33:45.000", 25.75),
                dp("2022-05-25 20:33:45.234", 27.25),
                dp("2022-05-26 20:36:44.234", 75.52),
                dp("2022-05-27 20:36:44.234", 100.25),
                dp("2022-05-28 20:36:44.234", 50.24),
                dp("2022-05-29 20:36:44.234", 27.44)
            )
        )
    );
  }

  public static Stream<Arguments> globalAggregates() {
    final var input = List.of(
        dp("2022-05-24 20:33:45.000", 25.75),
        dp("2022-05-24 20:33:45.234", 27.25),
        dp("2022-05-24 20:36:46.234", 75.52),
        dp("2022-05-24 20:37:47.234", 53.25),
        dp("2022-05-24 20:38:44.234", 57.55)
    );

    return Stream.of(
        Arguments.of(input, "avg()", 47.864),
        Arguments.of(input, "min()", 25.75),
        Arguments.of(input, "max()", 75.52),
        Arguments.of(input, "count()", 5.0),
        Arguments.of(input, "sum()", 239.32),
        Arguments.of(input, "stddev()", 18.98234927505)
    );
  }

  public static Stream<Arguments> globalAggregatesSet() {
    final var input = List.of(
        dp("2022-05-24 20:33:45.000", 25.75),
        dp("2022-05-24 20:33:45.234", 27.25),
        dp("2022-05-24 20:36:46.234", 75.52),
        dp("2022-05-24 20:37:47.234", 53.25),
        dp("2022-05-24 20:38:44.234", 57.55)
    );

    return Stream.of(
        Arguments.of(input, "avg() AS s1,  min() AS s2, stddev() AS s3", "s1,s2,s3", new Double[] {47.864, 25.75, 18.98234927505}),
        Arguments.of(input, "max() AS s1", "s1", new Double[] {75.52}),
        Arguments.of(input, "count() AS s1, sum() AS sample3", "s1, sample3", new Double[] {5.0, 239.32})
    );
  }

  public static Stream<Arguments> localAggregates() {
    final var input = List.of(
        dp("2022-05-24 20:33:45.000", 25.75),
        dp("2022-05-24 20:33:45.234", 27.25),
        dp("2022-05-24 20:36:46.234", 75.52),
        dp("2022-05-24 20:37:47.234", 53.25),
        dp("2022-05-24 20:38:44.234", 57.55)
    );

    return Stream.of(
        Arguments.of(input, "avg(\"2022-05-23T20:33:45.000Z\", \"2022-05-24T20:33:44.000Z\")", 0.0),
        Arguments.of(input, "max(\"2022-05-23T20:37:47.234Z\" , \"2022-05-24T20:33:45.234Z\")", 27.25),
        Arguments.of(input, "min(\"2022-05-24T20:35:46.234Z\",   \"2022-05-24T20:40:44.000Z\")", 53.25),
        Arguments.of(input, "count(\"2022-05-24T20:30:45.000Z\"\r,\n\"2022-05-24T20:37:47.233Z\")", 3.0),
        Arguments.of(input, "sum(\"2022-05-24T20:33:45.234Z\"\n ,  \"2022-05-24T20:36:46.234Z\")", 102.77),
        Arguments.of(input, "sum(\"\"\n ,  \"2022-05-24T20:36:46.234Z\")", 128.52),
        Arguments.of(input, "min(\"2022-05-24T20:35:46.234Z\",   \"\")", 53.25),
        Arguments.of(input, "sum(\"2022-05-24T20:35:46.234Z\",   \"\")", 186.32),
        Arguments.of(input, "avg(\"\",   \"\")", 47.864),
        Arguments.of(input, "stddev(\"\", \"2022-05-24T20:37:00.000Z\")", 23.116362170549),
        Arguments.of(input, "stddev(\"2022-05-24T20:33:45.100Z\", \"2022-05-24T20:38:00.000Z\")", 19.725746852499)
    );
  }

  public static Stream<Arguments> localAggregatesSet() {
    final var input = List.of(
        dp("2022-05-24 20:33:45.000", 25.75),
        dp("2022-05-24 20:33:45.234", 27.25),
        dp("2022-05-24 20:36:46.234", 75.52),
        dp("2022-05-24 20:37:47.234", 53.25),
        dp("2022-05-24 20:38:44.234", 57.55)
    );

    return Stream.of(
        Arguments.of(
            input, """
                avg("2022-05-23T20:33:45.000Z", "2022-05-24T20:33:44.000Z") AS s1,
                max("2022-05-23T20:37:47.234Z" , "2022-05-24T20:33:45.234Z") AS numeroDos,
                min("2022-05-24T20:35:46.234Z",   "2022-05-24T20:40:44.000Z") AS sampleNumber3,
                count(""
                ,
                "2022-05-24T20:36:46.233Z") AS fourthOneIsTheCharm""",
            "s1, numeroDos,   sampleNumber3,fourthOneIsTheCharm", new Double[] {0.0, 27.25, 53.25, 2.0}
        ),
        Arguments.of(
            input,
            "sum(\"2022-05-24T20:33:45.234Z\"\n ,  \"2022-05-24T20:36:46.234Z\") AS s1, "
                + "stddev(\"2022-05-24T20:33:45.100Z\", \"2022-05-24T20:38:00.000Z\") AS s2",
            "s1, s2",
            new Double[] {102.77, 19.725746852499}
        )
    );
  }

  private static DataPoint dp(Instant instant, Double value) {
    return DataPoint.of(instant, value);
  }

  private static DataPoint dp(String dateTime, Double value) {
    return DataPoint.of(INSTANT_FORMATTER.parse(dateTime, Instant::from), value);
  }

  private static DataPoint dp(Double value) {
    return DataPoint.of(Instant.now(), value);
  }
}
