package org.tsdl.implementation.parsing;

import org.junit.jupiter.api.Test;
import org.tsdl.implementation.model.TsdlOperator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TsdlListenerTest {
    @Test
    void tsdlListener_operatorGtAndThresholdDouble_parsesBothCorrectly() {
        String queryString = """
          operator=gt
          threshold=23.9
          """;

        var query = TsdlParser.INSTANCE.parseQuery(queryString);
        assertThat(query.operator()).isEqualTo(TsdlOperator.GT);
        assertThat(query.threshold()).isEqualTo(23.9d);
    }

    @Test
    void tsdlListener_integerThresholdAndOperatorLt_parsesBothCorrectly() {
        String queryString = """
          threshold=32582
          operator=lt
          """;

        var query = TsdlParser.INSTANCE.parseQuery(queryString);
        assertThat(query.operator()).isEqualTo(TsdlOperator.LT);
        assertThat(query.threshold()).isEqualTo(32582L);
    }

    @Test
    void tsdlListener_thresholdLineTwice_acceptsSecondOccurrence() {
        String queryString = """
          threshold=32582
          operator=lt
          threshold=2
          """;

        var query = TsdlParser.INSTANCE.parseQuery(queryString);
        assertThat(query.operator()).isEqualTo(TsdlOperator.LT);
        assertThat(query.threshold()).isEqualTo(2L);
    }

    @Test
    void tsdlListener_operatorLineTwice_acceptsSecondOccurrence() {
        String queryString = """
          threshold=32582
          operator=lt
          operator=gt
          """;

        var query = TsdlParser.INSTANCE.parseQuery(queryString);
        assertThat(query.operator()).isEqualTo(TsdlOperator.GT);
        assertThat(query.threshold()).isEqualTo(32582L);
    }

    @Test
    void tsdlListener_negativeLongThreshold_parsesCorrectly() {
        String queryString = """
          threshold=-3
          operator=lt
          """;

        var query = TsdlParser.INSTANCE.parseQuery(queryString);
        assertThat(query.operator()).isEqualTo(TsdlOperator.LT);
        assertThat(query.threshold()).isEqualTo(-3L);
    }

    @Test
    void tsdlListener_negativeDoubleThreshold_parsesCorrectly() {
        String queryString = """
          threshold=-3.25
          operator=lt
          """;

        var query = TsdlParser.INSTANCE.parseQuery(queryString);
        assertThat(query.operator()).isEqualTo(TsdlOperator.LT);
        assertThat(query.threshold()).isEqualTo(-3.25d);
    }

    @Test
    void tsdlListener_explicitPlusSignLongThreshold_throws() {
        String queryString = """
          threshold=+3
          operator=lt
          """;

        assertThatThrownBy(() -> TsdlParser.INSTANCE.parseQuery(queryString)).isInstanceOf(TsdlParserException.class);
    }

    @Test
    void tsdlListener_explicitPlusSignDoubleThreshold_throws() {
        String queryString = """
          threshold=+3.25
          operator=lt
          """;

        assertThatThrownBy(() -> TsdlParser.INSTANCE.parseQuery(queryString)).isInstanceOf(TsdlParserException.class);
    }

    @Test
    void tsdlListener_missingThreshold_throws() {
        String queryString = """
          threshold=-3.25
          """;

        assertThatThrownBy(() -> TsdlParser.INSTANCE.parseQuery(queryString)).isInstanceOf(TsdlParserException.class);
    }

    @Test
    void tsdlListener_missingOperator_throws() {
        String queryString = """
          operator=gt
          """;

        assertThatThrownBy(() -> TsdlParser.INSTANCE.parseQuery(queryString)).isInstanceOf(TsdlParserException.class);
    }

    @Test
    void tsdlListener_incorrectThresholdCommaSeparator_throws() {
        String queryString = """
          operator=gt
          threshold=2,47
          """;

        assertThatThrownBy(() -> TsdlParser.INSTANCE.parseQuery(queryString)).isInstanceOf(TsdlParserException.class);
    }
}
