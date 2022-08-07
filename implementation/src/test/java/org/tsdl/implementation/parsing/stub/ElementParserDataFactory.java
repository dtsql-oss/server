package org.tsdl.implementation.parsing.stub;

import java.time.Instant;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import org.tsdl.implementation.model.event.EventDurationBound;
import org.tsdl.implementation.model.event.EventDurationUnit;
import org.tsdl.implementation.model.result.YieldFormat;
import org.tsdl.implementation.parsing.TsdlElementParser;
import org.tsdl.implementation.parsing.enums.AggregatorType;
import org.tsdl.implementation.parsing.enums.ConnectiveIdentifier;
import org.tsdl.implementation.parsing.enums.DeviationFilterType;
import org.tsdl.implementation.parsing.enums.TemporalFilterType;
import org.tsdl.implementation.parsing.enums.TemporalRelationType;
import org.tsdl.implementation.parsing.enums.ThresholdFilterType;

@SuppressWarnings("unused") // only referenced by string literals, therefore usage unrecognized
public final class ElementParserDataFactory {
  private ElementParserDataFactory() {
  }

  public static Stream<Arguments> validConnectiveIdentifierInputs() {
    return Stream.of(
        Arguments.of("AND", ConnectiveIdentifier.AND),
        Arguments.of("OR", ConnectiveIdentifier.OR)
    );
  }

  public static Stream<Arguments> validThresholdFilterTypeInputs() {
    return Stream.of(
        Arguments.of("gt", ThresholdFilterType.GT),
        Arguments.of("lt", ThresholdFilterType.LT)
    );
  }

  public static Stream<Arguments> validDeviationFilterTypeInputs() {
    return Stream.of(
        Arguments.of("around", "rel", DeviationFilterType.AROUND_RELATIVE),
        Arguments.of("around", "abs", DeviationFilterType.AROUND_ABSOLUTE)
    );
  }

  public static Stream<Arguments> validTemporalFilterTypeInputs() {
    return Stream.of(
        Arguments.of("before", TemporalFilterType.BEFORE),
        Arguments.of("after", TemporalFilterType.AFTER)
    );
  }

  public static Stream<Arguments> validResultFormatInputs() {
    return Stream.of(
        Arguments.of("all periods", YieldFormat.ALL_PERIODS),
        Arguments.of("longest period", YieldFormat.LONGEST_PERIOD),
        Arguments.of("shortest period", YieldFormat.SHORTEST_PERIOD),
        Arguments.of("data points", YieldFormat.DATA_POINTS),
        Arguments.of("sample", YieldFormat.SAMPLE),
        Arguments.of("sample myAvg", YieldFormat.SAMPLE),
        Arguments.of("samples myAvg", YieldFormat.SAMPLE_SET),
        Arguments.of("samples myAvg, mySum", YieldFormat.SAMPLE_SET),
        Arguments.of("samples myAvg, mySum,myMin", YieldFormat.SAMPLE_SET)
    );
  }

  public static Stream<Arguments> validAggregatorTypeInputs() {
    return Stream.of(
        Arguments.of("avg", AggregatorType.AVERAGE),
        Arguments.of("max", AggregatorType.MAXIMUM),
        Arguments.of("min", AggregatorType.MINIMUM),
        Arguments.of("sum", AggregatorType.SUM),
        Arguments.of("count", AggregatorType.COUNT),
        Arguments.of("integral", AggregatorType.INTEGRAL),
        Arguments.of("stddev", AggregatorType.STANDARD_DEVIATION)
    );
  }

  public static Stream<Arguments> validTemporalRelationTypeInputs() {
    return Stream.of(
        Arguments.of("follows", TemporalRelationType.FOLLOWS),
        Arguments.of("precedes", TemporalRelationType.PRECEDES)
    );
  }

  public static Stream<Arguments> validEventDurationBoundInputs() {
    return Stream.of(
        Arguments.of("[5", TsdlElementParser.DurationBoundType.LOWER_BOUND, EventDurationBound.of(5, true)),
        Arguments.of("[ 5", TsdlElementParser.DurationBoundType.LOWER_BOUND, EventDurationBound.of(5, true)),
        Arguments.of("[   0", TsdlElementParser.DurationBoundType.LOWER_BOUND, EventDurationBound.of(0, true)),
        Arguments.of("   2345)   ", TsdlElementParser.DurationBoundType.UPPER_BOUND, EventDurationBound.of(2345, false)),
        Arguments.of("   7   )", TsdlElementParser.DurationBoundType.UPPER_BOUND, EventDurationBound.of(7, false)),
        Arguments.of("   5]   ", TsdlElementParser.DurationBoundType.UPPER_BOUND, EventDurationBound.of(5, true)),
        Arguments.of("   (   035   ", TsdlElementParser.DurationBoundType.LOWER_BOUND, EventDurationBound.of(35, false)),
        Arguments.of("   [\n   35   ", TsdlElementParser.DurationBoundType.LOWER_BOUND, EventDurationBound.of(35, true)),
        Arguments.of("35   \r]\n", TsdlElementParser.DurationBoundType.UPPER_BOUND, EventDurationBound.of(35, true)),
        Arguments.of(" ] ", TsdlElementParser.DurationBoundType.UPPER_BOUND, EventDurationBound.of(Long.MAX_VALUE, true)),
        Arguments.of(")", TsdlElementParser.DurationBoundType.UPPER_BOUND, EventDurationBound.of(Long.MAX_VALUE, false))
    );
  }

  public static Stream<Arguments> validEventDurationUnitInputs() {
    return Stream.of(
        Arguments.of("weeks", EventDurationUnit.WEEKS),
        Arguments.of("days", EventDurationUnit.DAYS),
        Arguments.of("hours", EventDurationUnit.HOURS),
        Arguments.of("minutes", EventDurationUnit.MINUTES),
        Arguments.of("seconds", EventDurationUnit.SECONDS),
        Arguments.of("millis", EventDurationUnit.MILLISECONDS)
    );
  }

  public static Stream<Arguments> validParseNumberInputs() {
    return Stream.of(
        Arguments.of("2", 2.0),
        Arguments.of("0", 0.0),
        Arguments.of("0.0", 0.0),
        Arguments.of("-0.0", 0.0),
        Arguments.of("-0", 0.0),
        Arguments.of("23.5876", 23.5876),
        Arguments.of("128395", 128395.0),
        Arguments.of("-100.0", -100.0),
        Arguments.of("-123.45", -123.45)
    );
  }

  public static Stream<Arguments> validParseStringLiteralInputs() {
    final Function<String, Arguments> testCase = (str) -> Arguments.of("\"%s\"".formatted(str), str);
    return Stream.of(
        testCase.apply(""),
        testCase.apply("null"),
        testCase.apply(" "),
        testCase.apply("     "),
        testCase.apply("1"),
        testCase.apply("12342"),
        testCase.apply(" 1"),
        testCase.apply("e"),
        testCase.apply(" ereAef"),
        testCase.apply("ß9"),
        testCase.apply("ß "),
        testCase.apply("123456  7"),
        testCase.apply(" 2ab83 o ")
    );
  }

  public static Stream<Arguments> validParseDateLiteralInputs() {
    final Function<String, Arguments> testCase = (str) -> Arguments.of("\"%s\"".formatted(str), Instant.parse(str));
    return Stream.of(
        testCase.apply("2022-07-13T23:04:06.123Z"),
        testCase.apply("2022-07-13T23:04:06.123456Z"),
        testCase.apply("2022-07-13T23:04:06.123456789Z"),
        testCase.apply("2022-07-13T23:04:06.12345689Z"),
        testCase.apply("2022-07-13T23:04:06Z"),
        testCase.apply("2009-01-01T12:00:00+01:00"),
        testCase.apply("2009-06-30T18:30:00-02:30")
    );
  }
}
