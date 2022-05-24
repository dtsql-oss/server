package org.tsdl.implementation.evaluation.stub;

import org.junit.jupiter.params.provider.Arguments;
import org.tsdl.infrastructure.model.DataPoint;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings("unused") // only referenced by string literals, therefore usage unrecognized
public final class DataPointDataFactory {
    private DataPointDataFactory() {
    }

    public static Stream<Arguments> dataPoints_0() {
        return Stream.of(
          Arguments.of(
            List.of(
              dp(25.75),
              dp(27.25),
              dp(75.52)
            )
          )
        );
    }

    private static DataPoint dp(Double value) {
        return DataPoint.of(Instant.now(), value);
    }
}
