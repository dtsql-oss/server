package org.tsdl.implementation.parsing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.NoSuchElementException;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.tsdl.implementation.factory.ObjectFactory;
import org.tsdl.implementation.model.result.ResultFormat;
import org.tsdl.implementation.parsing.enums.AggregatorType;
import org.tsdl.implementation.parsing.enums.ConnectiveIdentifier;
import org.tsdl.implementation.parsing.enums.FilterType;
import org.tsdl.implementation.parsing.enums.TemporalRelationType;
import org.tsdl.implementation.parsing.exception.TsdlParserException;

class TsdlElementParserTest {
  private static final TsdlElementParser PARSER = ObjectFactory.INSTANCE.elementParser();

  // region parseConnectiveIdentifier

  private static Stream<Arguments> validConnectiveIdentifierInputs() {
    return Stream.of(
        Arguments.of("AND", ConnectiveIdentifier.AND),
        Arguments.of("OR", ConnectiveIdentifier.OR)
    );
  }

  private static Stream<Arguments> validFilterTypeInputs() {
    return Stream.of(
        Arguments.of("gt", FilterType.GT),
        Arguments.of("lt", FilterType.LT)
    );
  }

  private static Stream<Arguments> validResultFormatInputs() {
    return Stream.of(
        Arguments.of("all periods", ResultFormat.ALL_PERIODS),
        Arguments.of("longest period", ResultFormat.LONGEST_PERIOD),
        Arguments.of("shortest period", ResultFormat.SHORTEST_PERIOD),
        Arguments.of("data points", ResultFormat.DATA_POINTS)
    );
  }

  private static Stream<Arguments> validAggregatorTypeInputs() {
    return Stream.of(
        Arguments.of("avg", AggregatorType.AVERAGE),
        Arguments.of("max", AggregatorType.MAXIMUM),
        Arguments.of("min", AggregatorType.MINIMUM),
        Arguments.of("sum", AggregatorType.SUM)
    );
  }

  // endregion

  // region parseFilterType

  private static Stream<Arguments> validTemporalRelationTypeInputs() {
    return Stream.of(
        Arguments.of("follows", TemporalRelationType.FOLLOWS),
        Arguments.of("precedes", TemporalRelationType.PRECEDES)
    );
  }

  private static Stream<Arguments> validParseNumberInputs() {
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

  @ParameterizedTest
  @MethodSource("org.tsdl.implementation.parsing.TsdlElementParserTest#validConnectiveIdentifierInputs")
  void parseConnectiveIdentifier_validRepresentations_ok(String representation, ConnectiveIdentifier member) {
    assertThat(PARSER.parseConnectiveIdentifier(representation)).isEqualTo(member);
  }

  @ParameterizedTest
  @ValueSource(strings = {"and", "or", "AnD", "OR ", "      ", "0", "1"})
  void parseConnectiveIdentifier_invalidRepresentations_throws(String representation) {
    assertThatThrownBy(() -> PARSER.parseConnectiveIdentifier(representation)).isInstanceOf(NoSuchElementException.class);
  }

  // endregion

  // region parseResultFormat

  @Test
  void parseConnectiveIdentifier_null_throws() {
    assertThatThrownBy(() -> PARSER.parseConnectiveIdentifier(null)).isInstanceOf(IllegalArgumentException.class);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.implementation.parsing.TsdlElementParserTest#validFilterTypeInputs")
  void parseFilterType_validRepresentations_ok(String representation, FilterType member) {
    assertThat(PARSER.parseFilterType(representation)).isEqualTo(member);
  }

  @ParameterizedTest
  @ValueSource(strings = {"lt ", " gt", "g t", "lT ", "", "      ", "0", "1"})
  void parseFilterType_invalidRepresentations_throws(String representation) {
    assertThatThrownBy(() -> PARSER.parseFilterType(representation)).isInstanceOf(NoSuchElementException.class);
  }

  @Test
  void parseFilterType_null_throws() {
    assertThatThrownBy(() -> PARSER.parseFilterType(null)).isInstanceOf(IllegalArgumentException.class);
  }

  // endregion

  // region parseAggregatorType

  @ParameterizedTest
  @MethodSource("org.tsdl.implementation.parsing.TsdlElementParserTest#validResultFormatInputs")
  void parseResultFormat_validRepresentations_ok(String representation, ResultFormat member) {
    assertThat(PARSER.parseResultFormat(representation)).isEqualTo(member);
  }

  @ParameterizedTest
  @ValueSource(strings = {"ALL periods", "longestperiod", "shortest perioD", "", "      ", "0", "1"})
  void parseResultFormat_invalidRepresentations_throws(String representation) {
    assertThatThrownBy(() -> PARSER.parseResultFormat(representation)).isInstanceOf(NoSuchElementException.class);
  }

  @Test
  void parseResultFormat_null_throws() {
    assertThatThrownBy(() -> PARSER.parseResultFormat(null)).isInstanceOf(IllegalArgumentException.class);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.implementation.parsing.TsdlElementParserTest#validAggregatorTypeInputs")
  void parseAggregatorType_validRepresentations_ok(String representation, AggregatorType member) {
    assertThat(PARSER.parseAggregatorType(representation)).isEqualTo(member);
  }

  // endregion

  // region parseTemporalRelationType

  @ParameterizedTest
  @ValueSource(strings = {"avg ", "average", "maX", "min ", "", "SUM", "      ", "0", "1"})
  void parseAggregatorType_invalidRepresentations_throws(String representation) {
    assertThatThrownBy(() -> PARSER.parseAggregatorType(representation)).isInstanceOf(NoSuchElementException.class);
  }

  @Test
  void parseAggregatorType_null_throws() {
    assertThatThrownBy(() -> PARSER.parseAggregatorType(null)).isInstanceOf(IllegalArgumentException.class);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.implementation.parsing.TsdlElementParserTest#validTemporalRelationTypeInputs")
  void parseTemporalRelationType_validRepresentations_ok(String representation, TemporalRelationType member) {
    assertThat(PARSER.parseTemporalRelationType(representation)).isEqualTo(member);
  }

  @ParameterizedTest
  @ValueSource(strings = {"folLows", "precedes ", " follows", " ", "", "      ", "0", "1"})
  void parseTemporalRelationType_invalidRepresentations_throws(String representation) {
    assertThatThrownBy(() -> PARSER.parseTemporalRelationType(representation)).isInstanceOf(NoSuchElementException.class);
  }

  // endregion

  // region parseNumber

  @Test
  void parseTemporalRelationType_null_throws() {
    assertThatThrownBy(() -> PARSER.parseTemporalRelationType(null)).isInstanceOf(IllegalArgumentException.class);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.implementation.parsing.TsdlElementParserTest#validParseNumberInputs")
  void parseNumber_validNumber_parsesCorrectly(String str, Double expected) {
    assertThat(PARSER.parseNumber(str)).isEqualTo(expected);
  }

  @ValueSource(strings = {
      "2,9", "2,9 ", "2.9 ", "29 ",
      "293.222,9", "19.283.932", "28,292,833", "2,233.3", "16 325,62", "16 325.62",
      "1,6326E+004", "1.6326E+004", "13%", "13 %", "13€", "13 €", "13 $", "13$", "$13",
      "NaN", "nan", "-NaN", "-nan", "Inf", "inf", "-Inf", "-inf", "Infinity", "infinity", "-Infinity", "-infinity"
  })
  @ParameterizedTest
  void parseNumber_invalidNumber_throws(String str) {
    assertThatThrownBy(() -> PARSER.parseNumber(str)).isInstanceOf(TsdlParserException.class);
  }

  @Test
  void parseNumber_null_throws() {
    assertThatThrownBy(() -> PARSER.parseNumber(null)).isInstanceOf(IllegalArgumentException.class);
  }

  // endregion
}
