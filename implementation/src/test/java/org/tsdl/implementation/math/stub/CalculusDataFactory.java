package org.tsdl.implementation.math.stub;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import org.tsdl.infrastructure.common.TsdlTimeUnit;
import org.tsdl.infrastructure.model.DataPoint;

@SuppressWarnings("unused") // only referenced by string literals, therefore usage unrecognized
public final class CalculusDataFactory {
  private CalculusDataFactory() {
  }

  public static Stream<Arguments> derivativeInputs() {
    return Stream.of(
        Arguments.of(
            List.of(
                DataPoint.of(Instant.parse("2022-08-25T10:00:00.000Z"), 10.0),
                DataPoint.of(Instant.parse("2022-08-25T10:15:00.000Z"), 2.0),
                DataPoint.of(Instant.parse("2022-08-25T10:30:00.000Z"), 3.0),
                DataPoint.of(Instant.parse("2022-08-25T10:45:00.000Z"), 5.0),
                DataPoint.of(Instant.parse("2022-08-25T11:00:00.000Z"), 4.0),
                DataPoint.of(Instant.parse("2022-08-25T11:15:00.000Z"), 6.0),
                DataPoint.of(Instant.parse("2022-08-25T11:30:00.000Z"), 18.0),
                DataPoint.of(Instant.parse("2022-08-25T11:45:00.000Z"), 14.0)
            ),
            TsdlTimeUnit.MINUTES,
            List.of(
                DataPoint.of(Instant.parse("2022-08-25T10:00:00.000Z"), -0.5333333333),
                DataPoint.of(Instant.parse("2022-08-25T10:15:00.000Z"), 0.0666666667),
                DataPoint.of(Instant.parse("2022-08-25T10:30:00.000Z"), 0.1333333333),
                DataPoint.of(Instant.parse("2022-08-25T10:45:00.000Z"), -0.0666666667),
                DataPoint.of(Instant.parse("2022-08-25T11:00:00.000Z"), 0.1333333333),
                DataPoint.of(Instant.parse("2022-08-25T11:15:00.000Z"), 0.8000000000),
                DataPoint.of(Instant.parse("2022-08-25T11:30:00.000Z"), -0.2666666667)
            )
        )
    );
  }
}
