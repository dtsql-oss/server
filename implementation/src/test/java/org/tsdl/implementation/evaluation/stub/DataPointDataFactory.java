package org.tsdl.implementation.evaluation.stub;

import org.junit.jupiter.params.provider.Arguments;
import org.tsdl.infrastructure.model.DataPoint;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings("unused") // only referenced by string literals, therefore usage unrecognized
public final class DataPointDataFactory {
    private DataPointDataFactory() {
    }

    private static final DateTimeFormatter INSTANT_FORMATTER = DateTimeFormatter
      .ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
      .withZone(ZoneOffset.UTC);

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
