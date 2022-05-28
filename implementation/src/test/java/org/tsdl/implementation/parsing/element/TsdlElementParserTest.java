package org.tsdl.implementation.parsing.element;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.tsdl.implementation.model.result.ResultFormat;

import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TsdlElementParserTest extends BaseElementParserTest {
    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.parsing.element.TsdlElementParserTest#provideValidTestInputs")
    void parseResultFormat_validRepresentations_ok(String representation, ResultFormat member) {
        assertThat(parser.parseResultFormat(representation)).isEqualTo(member);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ALL periods", "longestperiod", "shortest perioD", "", "      ", "0", "1"})
    void parseResultFormat_invalidRepresentations_throws(String representation) {
        assertThatThrownBy(() -> parser.parseResultFormat(representation)).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void parseResultFormat_null_throws() {
        assertThatThrownBy(() -> parser.parseResultFormat(null)).isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream<Arguments> provideValidTestInputs() {
        return Stream.of(
          Arguments.of("all periods", ResultFormat.ALL_PERIODS),
          Arguments.of("longest period", ResultFormat.LONGEST_PERIOD),
          Arguments.of("shortest period", ResultFormat.SHORTEST_PERIOD),
          Arguments.of("data points", ResultFormat.DATA_POINTS)
        );
    }
}
