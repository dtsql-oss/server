package org.tsdl.implementation.parsing.query;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.tsdl.implementation.model.result.ResultFormat;
import org.tsdl.implementation.parsing.exception.TsdlParserException;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TsdlQueryParserYieldDeclarationTest extends BaseQueryParserTest {

    @ParameterizedTest
    @MethodSource("org.tsdl.implementation.parsing.query.TsdlQueryParserYieldDeclarationTest#provideValidTestInputs")
    void yieldDeclaration_validRepresentations_parsed(String representation, ResultFormat member) {
        var queryString = "YIELD: %s".formatted(representation);
        var query = parser.parseQuery(queryString);

        assertThat(query.yield()).isEqualTo(member);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ALL periods", "longestperiod", "shortest perioD", "", "      ", "0", "1"})
    void yieldDeclaration_invalidRepresentations_throws(String representation) {
        var queryString = "YIELD: %s".formatted(representation);
        assertThatThrownBy(() -> parser.parseQuery(queryString)).isInstanceOf(TsdlParserException.class);
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
