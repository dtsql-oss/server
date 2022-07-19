package org.tsdl.implementation.parsing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.NoSuchElementException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.tsdl.implementation.factory.ObjectFactory;
import org.tsdl.implementation.model.result.YieldFormat;
import org.tsdl.implementation.parsing.enums.AggregatorType;
import org.tsdl.implementation.parsing.enums.ConnectiveIdentifier;
import org.tsdl.implementation.parsing.enums.FilterType;
import org.tsdl.implementation.parsing.enums.TemporalRelationType;
import org.tsdl.implementation.parsing.exception.TsdlParserException;

class TsdlElementParserTest {
  private static final TsdlElementParser PARSER = ObjectFactory.INSTANCE.elementParser();

  @ParameterizedTest
  @MethodSource("org.tsdl.implementation.parsing.stub.ElementParserDataFactory#validConnectiveIdentifierInputs")
  void parseConnectiveIdentifier_validRepresentations_ok(String representation, ConnectiveIdentifier member) {
    assertThat(PARSER.parseConnectiveIdentifier(representation)).isEqualTo(member);
  }

  @ParameterizedTest
  @ValueSource(strings = {"and", "or", "AnD", "OR ", "      ", "0", "1"})
  void parseConnectiveIdentifier_invalidRepresentations_throws(String representation) {
    assertThatThrownBy(() -> PARSER.parseConnectiveIdentifier(representation)).isInstanceOf(NoSuchElementException.class);
  }

  @Test
  void parseConnectiveIdentifier_null_throws() {
    assertThatThrownBy(() -> PARSER.parseConnectiveIdentifier(null)).isInstanceOf(IllegalArgumentException.class);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.implementation.parsing.stub.ElementParserDataFactory#validFilterTypeInputs")
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

  @ParameterizedTest
  @MethodSource("org.tsdl.implementation.parsing.stub.ElementParserDataFactory#validResultFormatInputs")
  void parseResultFormat_validRepresentations_ok(String representation, YieldFormat member) {
    assertThat(PARSER.parseResultFormat(representation)).isEqualTo(member);
  }

  @ParameterizedTest
  @ValueSource(strings = {"ALL periods", "longestperiod", "all data points", "sampleset", "samplee", "shortest perioD", "", "      ", "0", "1"})
  void parseResultFormat_invalidRepresentations_throws(String representation) {
    assertThatThrownBy(() -> PARSER.parseResultFormat(representation)).isInstanceOf(NoSuchElementException.class);
  }

  @Test
  void parseResultFormat_null_throws() {
    assertThatThrownBy(() -> PARSER.parseResultFormat(null)).isInstanceOf(IllegalArgumentException.class);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.implementation.parsing.stub.ElementParserDataFactory#validAggregatorTypeInputs")
  void parseAggregatorType_validRepresentations_ok(String representation, AggregatorType member) {
    assertThat(PARSER.parseAggregatorType(representation)).isEqualTo(member);
  }

  @ParameterizedTest
  @ValueSource(strings = {"avg ", "average", "maX", "min ", "", "SUM", "cnt", "      ", "0", "1"})
  void parseAggregatorType_invalidRepresentations_throws(String representation) {
    assertThatThrownBy(() -> PARSER.parseAggregatorType(representation)).isInstanceOf(NoSuchElementException.class);
  }

  @Test
  void parseAggregatorType_null_throws() {
    assertThatThrownBy(() -> PARSER.parseAggregatorType(null)).isInstanceOf(IllegalArgumentException.class);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.implementation.parsing.stub.ElementParserDataFactory#validTemporalRelationTypeInputs")
  void parseTemporalRelationType_validRepresentations_ok(String representation, TemporalRelationType member) {
    assertThat(PARSER.parseTemporalRelationType(representation)).isEqualTo(member);
  }

  @ParameterizedTest
  @ValueSource(strings = {"folLows", "precedes ", " follows", " ", "", "      ", "0", "1"})
  void parseTemporalRelationType_invalidRepresentations_throws(String representation) {
    assertThatThrownBy(() -> PARSER.parseTemporalRelationType(representation)).isInstanceOf(NoSuchElementException.class);
  }

  @Test
  void parseTemporalRelationType_null_throws() {
    assertThatThrownBy(() -> PARSER.parseTemporalRelationType(null)).isInstanceOf(IllegalArgumentException.class);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.implementation.parsing.stub.ElementParserDataFactory#validParseNumberInputs")
  void parseNumber_validNumber_parsesCorrectly(String str, Double expected) {
    assertThat(PARSER.parseNumber(str)).isEqualTo(expected);
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "2,9", "2,9 ", "2.9 ", "29 ",
      "293.222,9", "19.283.932", "28,292,833", "2,233.3", "16 325,62", "16 325.62",
      "1,6326E+004", "1.6326E+004", "13%", "13 %", "13€", "13 €", "13 $", "13$", "$13",
      "NaN", "nan", "-NaN", "-nan", "Inf", "inf", "-Inf", "-inf", "Infinity", "infinity", "-Infinity", "-infinity"
  })
  void parseNumber_invalidNumber_throws(String str) {
    assertThatThrownBy(() -> PARSER.parseNumber(str)).isInstanceOf(TsdlParserException.class);
  }

  @Test
  void parseNumber_null_throws() {
    assertThatThrownBy(() -> PARSER.parseNumber(null)).isInstanceOf(IllegalArgumentException.class);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.implementation.parsing.stub.ElementParserDataFactory#validParseStringLiteralInputs")
  @Disabled
  void parseStringLiteral_validLiteral_parsesCorrectly(String str, String expected) {
    assertThat(PARSER.parseStringLiteral(str)).isEqualTo(expected);
  }

  @ParameterizedTest
  @ValueSource(strings = {})
  @Disabled
  void parseStringLiteral_invalidLiteral_throws(String str) {
    assertThatThrownBy(() -> PARSER.parseStringLiteral(str)).isInstanceOf(TsdlParserException.class);
  }

  @Test
  @Disabled
  void parseStringLiteral_null_throws() {
    assertThatThrownBy(() -> PARSER.parseStringLiteral(null)).isInstanceOf(IllegalArgumentException.class);
  }
}
