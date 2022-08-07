package org.tsdl.implementation.formatting.stub;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import org.tsdl.implementation.factory.TsdlComponentFactory;
import org.tsdl.implementation.factory.TsdlQueryElementFactory;
import org.tsdl.implementation.parsing.enums.AggregatorType;
import org.tsdl.infrastructure.model.DataPoint;

@SuppressWarnings("unused") // only referenced by string literals, therefore usage unrecognized
public final class FormattingDataFactory {
  private static final TsdlQueryElementFactory ELEMENTS = TsdlComponentFactory.INSTANCE.elementFactory();
  private static final TsdlComponentFactory COMPONENTS = TsdlComponentFactory.INSTANCE;

  private static final DateTimeFormatter INSTANT_FORMATTER = DateTimeFormatter
      .ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
      .withZone(ZoneOffset.UTC);

  private FormattingDataFactory() {
  }

  public static Stream<Arguments> sampleArgs() {
    var dps = List.of(
        dp("2022-05-24 20:33:45.000", 25.75),
        dp("2022-05-24 20:33:45.234", 27.25),
        dp("2022-05-24 20:36:44.234", 75.52)
    );

    return Stream.of(
        Arguments.of(
            dps,
            ELEMENTS.getAggregator(AggregatorType.COUNT, null, null, COMPONENTS.summaryStatistics()),
            "myCount",
            new String[] {"0"},
            "sample count() with ID 'myCount' := 3"
        ),
        Arguments.of(
            dps,
            ELEMENTS.getAggregator(AggregatorType.SUM, null, null, COMPONENTS.summaryStatistics()),
            "mySum",
            new String[] {"2.0"},
            "sample sum() with ID 'mySum' := 128.52"
        ),
        Arguments.of(
            dps,
            ELEMENTS.getAggregator(AggregatorType.MINIMUM, Instant.parse("2022-05-24T20:33:45.000Z"), null, COMPONENTS.summaryStatistics()),
            "myMin",
            new String[] {"1"},
            "sample min(\"2022-05-24T20:33:45Z\", \"\") with ID 'myMin' := 25.8"
        ),
        Arguments.of(
            dps,
            ELEMENTS.getAggregator(AggregatorType.MAXIMUM, null, Instant.parse("2022-05-24T20:36:44.234Z"), COMPONENTS.summaryStatistics()),
            "myMax",
            new String[] {"3"},
            "sample max(\"\", \"2022-05-24T20:36:44.234Z\") with ID 'myMax' := 75.520"
        ),
        Arguments.of(
            dps,
            ELEMENTS.getAggregator(AggregatorType.AVERAGE, Instant.parse("2022-05-24T20:33:45.000Z"), Instant.parse("2022-05-24T20:37:44.234Z"),
                COMPONENTS.summaryStatistics()),
            "myAvg",
            new String[] {"1"},
            "sample avg(\"2022-05-24T20:33:45Z\", \"2022-05-24T20:37:44.234Z\") with ID 'myAvg' := 42.8"
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
