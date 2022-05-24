package org.tsdl.storage.csv.stub;

import org.junit.jupiter.params.provider.Arguments;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings("unused") // only referenced by string literals, therefore usage unrecognized
public final class CsvRowDataFactory {
    private CsvRowDataFactory() {
    }

    private static final DateTimeFormatter INSTANT_FORMATTER = DateTimeFormatter
      .ofPattern("MM/dd/yyyy HH:mm:ss")
      .withZone(ZoneId.systemDefault());

    public static Stream<Arguments> threeEntries() {
        return Stream.of(
          Arguments.of(List.of(
              List.of(23, "value1", formatInstant(Instant.now())),
              List.of(24, "value2", formatInstant(Instant.now())),
              List.of(25, "value3", formatInstant(Instant.now()))
            )
          )
        );
    }

    private static String formatInstant(Instant instant) {
        return INSTANT_FORMATTER.format(instant);
    }
}
