package org.tsdl.storage.influxdb.stub;

import org.junit.jupiter.params.provider.Arguments;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;


@SuppressWarnings("unused") // only referenced by string literals, therefore usage unrecognized
public final class FluxTableDataFactory {
    private FluxTableDataFactory() {
    }

    public static Stream<Arguments> threeSingletonTables() {
        return Stream.of(
          Arguments.of(List.of(
              // table 0
              List.of(
                List.of(formatInstant(Instant.now()), 23)
              ),

              // table 1
              List.of(
                List.of(formatInstant(Instant.now()), 24)
              ),

              // table 2
              List.of(
                List.of(formatInstant(Instant.now()), 25)
              )
            )
          )
        );
    }

    private static String formatInstant(Instant instant) {
        return DateTimeFormatter.ISO_INSTANT.format(instant);
    }
}
