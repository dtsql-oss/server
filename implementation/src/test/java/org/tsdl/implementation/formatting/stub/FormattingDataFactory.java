package org.tsdl.implementation.formatting.stub;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import org.tsdl.implementation.parsing.enums.AggregatorType;
import org.tsdl.infrastructure.model.DataPoint;

@SuppressWarnings("unused") // only referenced by string literals, therefore usage unrecognized
public final class FormattingDataFactory {
  private static final DateTimeFormatter INSTANT_FORMATTER = DateTimeFormatter
      .ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
      .withZone(ZoneOffset.UTC);

  private FormattingDataFactory() {
  }

  public static Stream<Arguments> sampleArgsStream() {
    var dps = List.of(
        dp("2022-05-24 20:33:45.000", 25.75),
        dp("2022-05-24 20:33:45.234", 27.25),
        dp("2022-05-24 20:36:44.234", 75.52)
    );

    return Stream.of(
        Arguments.of(dps, AggregatorType.COUNT, "myCount", new String[] {"0"}, "sample 'myCount' of 'count' aggregator := 3\n"),
        Arguments.of(dps, AggregatorType.SUM, "mySum", new String[] {"2.0"}, "sample 'mySum' of 'sum' aggregator := 128.52\n"),
        Arguments.of(dps, AggregatorType.MINIMUM, "myMin", new String[] {"1"}, "sample 'myMin' of 'min' aggregator := 25.8\n"),
        Arguments.of(dps, AggregatorType.MAXIMUM, "myMax", new String[] {"3"}, "sample 'myMax' of 'max' aggregator := 75.520\n"),
        Arguments.of(dps, AggregatorType.AVERAGE, "myAvg", new String[] {"1"}, "sample 'myAvg' of 'avg' aggregator := 42.8\n")
    );
  }

  public static Stream<Arguments> sampleArgsList() {
    var dps = List.of(
        dp("2022-05-24 20:33:45.000", 25.75),
        dp("2022-05-24 20:33:45.234", 27.25),
        dp("2022-05-24 20:36:44.234", 75.52)
    );

    return Stream.of(
        Arguments.of(dps, AggregatorType.COUNT, "myCount", new String[] {"0"}, "sample 'myCount' of 'count' aggregator := 3"),
        Arguments.of(dps, AggregatorType.SUM, "mySum", new String[] {"2.0"}, "sample 'mySum' of 'sum' aggregator := 128.52"),
        Arguments.of(dps, AggregatorType.MINIMUM, "myMin", new String[] {"1"}, "sample 'myMin' of 'min' aggregator := 25.8"),
        Arguments.of(dps, AggregatorType.MAXIMUM, "myMax", new String[] {"3"}, "sample 'myMax' of 'max' aggregator := 75.520"),
        Arguments.of(dps, AggregatorType.AVERAGE, "myAvg", new String[] {"1"}, "sample 'myAvg' of 'avg' aggregator := 42.8")
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
