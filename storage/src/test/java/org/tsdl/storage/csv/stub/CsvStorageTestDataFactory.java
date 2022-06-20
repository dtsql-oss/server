package org.tsdl.storage.csv.stub;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import org.tsdl.infrastructure.model.DataPoint;


@SuppressWarnings("unused") // only referenced by string literals, therefore usage unrecognized
public final class CsvStorageTestDataFactory {
  private static final DateTimeFormatter INSTANT_FORMATTER = DateTimeFormatter
      .ofPattern("MM/dd/yyyy HH:mm:ss")
      .withZone(ZoneOffset.UTC);

  private CsvStorageTestDataFactory() {
  }

  public static Stream<Arguments> threeCsvRows() {
    return Stream.of(
        Arguments.of(List.of(
                List.of(23, "value1", formatInstant(Instant.now())),
                List.of(24, "value2", formatInstant(Instant.now())),
                List.of(25, "value3", formatInstant(Instant.now()))
            )
        )
    );
  }

  public static Stream<Arguments> threeDataPoints() {
    return Stream.of(
        Arguments.of(List.of(
                DataPoint.of(getInstant("02/20/2018 09:25:04"), 8394.283846),
                DataPoint.of(getInstant("12/02/2019 16:27:19"), -98347383.0),
                DataPoint.of(getInstant("07/30/2021 23:12:54"), 363.2)
            )
        )
    );
  }

  private static Instant getInstant(String date) {
    return INSTANT_FORMATTER.parse(date, Instant::from);
  }

  private static String formatInstant(Instant instant) {
    return INSTANT_FORMATTER.format(instant);
  }
}
