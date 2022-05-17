package org.tsdl.implementation.parsing;

import org.junit.jupiter.api.Test;
import org.tsdl.implementation.factory.ObjectFactory;

public class TsdlParserTest {
    private final TsdlParser parser = ObjectFactory.INSTANCE.getParser();


    @Test
    void tsdlListener() {
        var queryString  = """
          FILTER:
            AND(gt(23.4), NOT(lt(-23.1)))
          YIELD *
          """;

        System.out.println(parser.parseQuery(queryString));
    }

    /*@Test
    void tsdlListener_operatorGtAndThresholdDouble_parsesBothCorrectly() {
        String queryString = """
          OR(lt,gt,gt,lt)
          """;

        var query = TsdlParserImpl.INSTANCE.parseQuery(queryString);
        assertThat(query.operator()).isEqualTo(FilterType.GT);
        assertThat(query.threshold()).isEqualTo(23.9d);
    }

    @Test
    void tsdlListener_integerThresholdAndOperatorLt_parsesBothCorrectly() {
        String queryString = """
          threshold=32582
          operator=lt
          """;

        var query = TsdlParserImpl.INSTANCE.parseQuery(queryString);
        assertThat(query.operator()).isEqualTo(FilterType.LT);
        assertThat(query.threshold()).isEqualTo(32582L);
    }

    @Test
    void tsdlListener_thresholdLineTwice_acceptsSecondOccurrence() {
        String queryString = """
          threshold=32582
          operator=lt
          threshold=2
          """;

        var query = TsdlParserImpl.INSTANCE.parseQuery(queryString);
        assertThat(query.operator()).isEqualTo(FilterType.LT);
        assertThat(query.threshold()).isEqualTo(2L);
    }

    @Test
    void tsdlListener_operatorLineTwice_acceptsSecondOccurrence() {
        String queryString = """
          threshold=32582
          operator=lt
          operator=gt
          """;

        var query = TsdlParserImpl.INSTANCE.parseQuery(queryString);
        assertThat(query.operator()).isEqualTo(FilterType.GT);
        assertThat(query.threshold()).isEqualTo(32582L);
    }

    @Test
    void tsdlListener_negativeLongThreshold_parsesCorrectly() {
        String queryString = """
          threshold=-3
          operator=lt
          """;

        var query = TsdlParserImpl.INSTANCE.parseQuery(queryString);
        assertThat(query.operator()).isEqualTo(FilterType.LT);
        assertThat(query.threshold()).isEqualTo(-3L);
    }

    @Test
    void tsdlListener_negativeDoubleThreshold_parsesCorrectly() {
        String queryString = """
          threshold=-3.25
          operator=lt
          """;

        var query = TsdlParserImpl.INSTANCE.parseQuery(queryString);
        assertThat(query.operator()).isEqualTo(FilterType.LT);
        assertThat(query.threshold()).isEqualTo(-3.25d);
    }

    @Test
    void tsdlListener_explicitPlusSignLongThreshold_throws() {
        String queryString = """
          threshold=+3
          operator=lt
          """;

        assertThatThrownBy(() -> TsdlParserImpl.INSTANCE.parseQuery(queryString)).isInstanceOf(TsdlParserException.class);
    }

    @Test
    void tsdlListener_explicitPlusSignDoubleThreshold_throws() {
        String queryString = """
          threshold=+3.25
          operator=lt
          """;

        assertThatThrownBy(() -> TsdlParserImpl.INSTANCE.parseQuery(queryString)).isInstanceOf(TsdlParserException.class);
    }

    @Test
    void tsdlListener_missingThreshold_throws() {
        String queryString = """
          threshold=-3.25
          """;

        assertThatThrownBy(() -> TsdlParserImpl.INSTANCE.parseQuery(queryString)).isInstanceOf(TsdlParserException.class);
    }

    @Test
    void tsdlListener_missingOperator_throws() {
        String queryString = """
          operator=gt
          """;

        assertThatThrownBy(() -> TsdlParserImpl.INSTANCE.parseQuery(queryString)).isInstanceOf(TsdlParserException.class);
    }

    @Test
    void tsdlListener_incorrectThresholdCommaSeparator_throws() {
        String queryString = """
          operator=gt
          threshold=2,47
          """;

        assertThatThrownBy(() -> TsdlParserImpl.INSTANCE.parseQuery(queryString)).isInstanceOf(TsdlParserException.class);
    }*/
}
