package org.tsdl.implementation.math.stub;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import org.tsdl.implementation.math.model.LinearModel;
import org.tsdl.infrastructure.model.DataPoint;

@SuppressWarnings("unused") // only referenced by string literals, therefore usage unrecognized
public final class ContinuousRegressionDataFactory {
  private ContinuousRegressionDataFactory() {
  }

  public static Stream<Arguments> linearLeastSquaresInput() {
    return Stream.of(
        Arguments.of(
            List.of(
                DataPoint.of(Instant.parse("2022-08-25T11:28:12.000Z"), 52.21),
                DataPoint.of(Instant.parse("2022-08-25T11:30:00.000Z"), 53.12),
                DataPoint.of(Instant.parse("2022-08-25T11:31:12.000Z"), 54.48),
                DataPoint.of(Instant.parse("2022-08-25T11:33:00.000Z"), 55.84),
                DataPoint.of(Instant.parse("2022-08-25T11:34:12.000Z"), 57.2),
                DataPoint.of(Instant.parse("2022-08-25T11:36:00.000Z"), 58.57),
                DataPoint.of(Instant.parse("2022-08-25T11:38:48.000Z"), 59.93),
                DataPoint.of(Instant.parse("2022-08-25T11:39:00.000Z"), 61.29),
                DataPoint.of(Instant.parse("2022-08-25T11:41:48.000Z"), 63.11),
                DataPoint.of(Instant.parse("2022-08-25T11:42:00.000Z"), 64.47),
                DataPoint.of(Instant.parse("2022-08-25T11:44:48.000Z"), 66.28),
                DataPoint.of(Instant.parse("2022-08-25T11:45:00.000Z"), 68.1),
                DataPoint.of(Instant.parse("2022-08-25T11:47:48.000Z"), 69.92),
                DataPoint.of(Instant.parse("2022-08-25T11:48:00.000Z"), 72.19),
                DataPoint.of(Instant.parse("2022-08-25T11:50:48.000Z"), 74.46)
            ),
            LinearModel.of(0.000016345, 51.1203),
            0.00005
        )
    );
  }
}
