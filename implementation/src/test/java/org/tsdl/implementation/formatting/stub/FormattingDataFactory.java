package org.tsdl.implementation.formatting.stub;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import org.tsdl.implementation.evaluation.impl.sample.aggregation.temporal.TimePeriodImpl;
import org.tsdl.implementation.factory.TsdlComponentFactory;
import org.tsdl.implementation.factory.TsdlQueryElementFactory;
import org.tsdl.implementation.model.common.ParsableTsdlTimeUnit;
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
            ELEMENTS.getAggregator(AggregatorType.COUNT, null, (Instant) null, COMPONENTS.summaryStatistics()),
            "myCount",
            new String[] {"0"},
            "sample 'myCount' count() := 3"
        ),
        Arguments.of(
            dps,
            ELEMENTS.getAggregator(AggregatorType.SUM, (Instant) null, null, COMPONENTS.summaryStatistics()),
            "mySum",
            new String[] {"2.0"},
            "sample 'mySum' sum() := 128.52"
        ),
        Arguments.of(
            dps,
            ELEMENTS.getAggregator(AggregatorType.MINIMUM, Instant.parse("2022-05-24T20:33:45.000Z"), null, COMPONENTS.summaryStatistics()),
            "myMin",
            new String[] {"1"},
            "sample 'myMin' min(\"2022-05-24T20:33:45Z\", \"\") := 25.8"
        ),
        Arguments.of(
            dps,
            ELEMENTS.getAggregator(AggregatorType.MAXIMUM, null, Instant.parse("2022-05-24T20:36:44.234Z"), COMPONENTS.summaryStatistics()),
            "myMax",
            new String[] {"3"},
            "sample 'myMax' max(\"\", \"2022-05-24T20:36:44.234Z\") := 75.520"
        ),
        Arguments.of(
            dps,
            ELEMENTS.getAggregator(AggregatorType.AVERAGE, Instant.parse("2022-05-24T20:33:45.000Z"), Instant.parse("2022-05-24T20:37:44.234Z"),
                COMPONENTS.summaryStatistics()),
            "myAvg",
            new String[] {"1"},
            "sample 'myAvg' avg(\"2022-05-24T20:33:45Z\", \"2022-05-24T20:37:44.234Z\") := 42.8"
        ),

        Arguments.of(
            dps,
            ELEMENTS.getAggregator(
                AggregatorType.TEMPORAL_AVERAGE,
                List.of(new TimePeriodImpl(Instant.parse("2022-05-24T20:33:45.000Z"), Instant.parse("2022-05-24T20:37:44.234Z"))),
                ParsableTsdlTimeUnit.MINUTES,
                COMPONENTS.summaryStatistics()
            ),
            "myTemporalAvg",
            new String[] {"0"},
            "sample 'myTemporalAvg' avg_t(minutes, \"2022-05-24T20:33:45Z/2022-05-24T20:37:44.234Z\") := 4 minutes"
        ),
        Arguments.of(
            dps,
            ELEMENTS.getAggregator(
                AggregatorType.TEMPORAL_MAXIMUM,
                List.of(new TimePeriodImpl(Instant.parse("2022-05-24T20:33:45.000Z"), Instant.parse("2022-05-24T20:37:44.234Z")),
                    new TimePeriodImpl(Instant.parse("2022-05-24T20:33:45.000Z"), Instant.parse("2022-05-24T20:33:45.234Z"))),
                ParsableTsdlTimeUnit.SECONDS,
                COMPONENTS.summaryStatistics()
            ),
            "myTemporalMax",
            new String[] {"3"},
            "sample 'myTemporalMax' max_t(seconds, \"2022-05-24T20:33:45Z/2022-05-24T20:37:44.234Z\", "
                + "\"2022-05-24T20:33:45Z/2022-05-24T20:33:45.234Z\") := 239.234 seconds"
        ),
        Arguments.of(
            dps,
            ELEMENTS.getAggregator(
                AggregatorType.TEMPORAL_MINIMUM,
                List.of(new TimePeriodImpl(Instant.parse("2022-05-24T20:33:45.000Z"), Instant.parse("2022-05-24T20:37:44.234Z")),
                    new TimePeriodImpl(Instant.parse("2022-05-24T20:33:45.000Z"), Instant.parse("2022-05-24T20:33:45.234Z"))),
                ParsableTsdlTimeUnit.MILLISECONDS,
                COMPONENTS.summaryStatistics()
            ),
            "myTemporalMin",
            new String[] {"5"},
            "sample 'myTemporalMin' min_t(millis, \"2022-05-24T20:33:45Z/2022-05-24T20:37:44.234Z\", "
                + "\"2022-05-24T20:33:45Z/2022-05-24T20:33:45.234Z\") := 234.00000 millis"
        ),
        Arguments.of(
            dps,
            ELEMENTS.getAggregator(
                AggregatorType.TEMPORAL_COUNT,
                List.of(new TimePeriodImpl(Instant.parse("2022-05-24T20:33:45.000Z"), Instant.parse("2022-05-24T20:37:44.234Z")),
                    new TimePeriodImpl(Instant.parse("2022-05-24T20:33:45.000Z"), Instant.parse("2022-05-24T20:33:45.234Z"))),
                null,
                COMPONENTS.summaryStatistics()
            ),
            "myTemporalCount",
            new String[] {"2"},
            "sample 'myTemporalCount' count_t(\"2022-05-24T20:33:45Z/2022-05-24T20:37:44.234Z\", "
                + "\"2022-05-24T20:33:45Z/2022-05-24T20:33:45.234Z\") := 2.00"
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
