package org.tsdl.implementation.parsing.stub;

import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import org.tsdl.implementation.model.result.YieldFormat;
import org.tsdl.implementation.parsing.enums.AggregatorType;
import org.tsdl.implementation.parsing.enums.ConnectiveIdentifier;
import org.tsdl.implementation.parsing.enums.FilterType;
import org.tsdl.implementation.parsing.enums.TemporalRelationType;

@SuppressWarnings("unused") // only referenced by string literals, therefore usage unrecognized
public final class ElementParserDataFactory {
  private ElementParserDataFactory() {}

  public static Stream<Arguments> validConnectiveIdentifierInputs() {
    return Stream.of(
        Arguments.of("AND", ConnectiveIdentifier.AND),
        Arguments.of("OR", ConnectiveIdentifier.OR)
    );
  }

  public static Stream<Arguments> validFilterTypeInputs() {
    return Stream.of(
        Arguments.of("gt", FilterType.GT),
        Arguments.of("lt", FilterType.LT)
    );
  }

  public static Stream<Arguments> validResultFormatInputs() {
    return Stream.of(
        Arguments.of("all periods", YieldFormat.ALL_PERIODS),
        Arguments.of("longest period", YieldFormat.LONGEST_PERIOD),
        Arguments.of("shortest period", YieldFormat.SHORTEST_PERIOD),
        Arguments.of("data points", YieldFormat.DATA_POINTS),
        Arguments.of("sample", YieldFormat.SAMPLE),
        Arguments.of("sample myAvg", YieldFormat.SAMPLE)
    );
  }

  public static Stream<Arguments> validAggregatorTypeInputs() {
    return Stream.of(
        Arguments.of("avg", AggregatorType.AVERAGE),
        Arguments.of("max", AggregatorType.MAXIMUM),
        Arguments.of("min", AggregatorType.MINIMUM),
        Arguments.of("sum", AggregatorType.SUM),
        Arguments.of("count", AggregatorType.COUNT)
    );
  }

  public static Stream<Arguments> validTemporalRelationTypeInputs() {
    return Stream.of(
        Arguments.of("follows", TemporalRelationType.FOLLOWS),
        Arguments.of("precedes", TemporalRelationType.PRECEDES)
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
}
