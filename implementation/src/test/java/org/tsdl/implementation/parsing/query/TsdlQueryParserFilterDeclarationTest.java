package org.tsdl.implementation.parsing.query;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.tsdl.implementation.model.connective.AndConnective;
import org.tsdl.implementation.model.connective.OrConnective;
import org.tsdl.implementation.model.filter.*;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class TsdlQueryParserFilterDeclarationTest extends BaseQueryParserTest {
    private static final Function<? super ThresholdFilter, Double> VALUE_EXTRACTOR = filter -> filter.threshold().value();

    @Test
    void filterDeclaration_conjunctiveFilterWithOneArgument() {
        var queryString = """
          FILTER:
            AND(gt(23.4))
          YIELD: data points
          """;

        var query = parser.parseQuery(queryString);

        assertThat(query.filter())
          .asInstanceOf(InstanceOfAssertFactories.type(AndConnective.class))
          .extracting(AndConnective::filters, InstanceOfAssertFactories.list(SinglePointFilter.class))
          .hasSize(1);

        assertThat(query.filter().filters().get(0))
          .asInstanceOf(InstanceOfAssertFactories.type(GtFilter.class))
          .extracting(VALUE_EXTRACTOR)
          .isEqualTo(23.4);
    }

    @Test
    void filterDeclaration_disjunctiveFilterWithOneArgument() {
        var queryString = """
          FILTER:
            OR(lt(-2.3))
          YIELD: data points
          """;

        var query = parser.parseQuery(queryString);

        assertThat(query.filter())
          .asInstanceOf(InstanceOfAssertFactories.type(OrConnective.class))
          .extracting(OrConnective::filters, InstanceOfAssertFactories.list(SinglePointFilter.class))
          .hasSize(1)
          .element(0, InstanceOfAssertFactories.type(LtFilter.class))
          .extracting(VALUE_EXTRACTOR)
          .isEqualTo(-2.3);
    }

    @Test
    void filterDeclaration_negatedFilter() {
        var queryString = """
          FILTER:
            OR(NOT(lt(25)))
          YIELD: data points
          """;

        var query = parser.parseQuery(queryString);

        assertThat(query.filter())
          .asInstanceOf(InstanceOfAssertFactories.type(OrConnective.class))
          .extracting(OrConnective::filters, InstanceOfAssertFactories.list(SinglePointFilter.class))
          .hasSize(1)
          .element(0, InstanceOfAssertFactories.type(NegatedSinglePointFilter.class))
          .extracting(NegatedSinglePointFilter::filter, InstanceOfAssertFactories.type(LtFilter.class))
          .extracting(VALUE_EXTRACTOR)
          .isEqualTo(25d);
    }

    @Test
    void filterDeclaration_multipleArguments() {
        var queryString = """
          FILTER:
            OR(
                NOT(lt(25.1)),       gt(3.4),
                NOT(gt(1000)),
                lt(-3.4447)
              )
          YIELD: data points
          """;

        var query = parser.parseQuery(queryString);

        assertThat(query.filter())
          .asInstanceOf(InstanceOfAssertFactories.type(OrConnective.class))
          .extracting(OrConnective::filters, InstanceOfAssertFactories.list(SinglePointFilter.class))
          .hasSize(4);

        assertThat(query.filter().filters().get(0))
          .asInstanceOf(InstanceOfAssertFactories.type(NegatedSinglePointFilter.class))
          .extracting(NegatedSinglePointFilter::filter, InstanceOfAssertFactories.type(LtFilter.class))
          .extracting(VALUE_EXTRACTOR)
          .isEqualTo(25.1);

        assertThat(query.filter().filters().get(1))
          .asInstanceOf(InstanceOfAssertFactories.type(GtFilter.class))
          .extracting(VALUE_EXTRACTOR)
          .isEqualTo(3.4);

        assertThat(query.filter().filters().get(2))
          .asInstanceOf(InstanceOfAssertFactories.type(NegatedSinglePointFilter.class))
          .extracting(NegatedSinglePointFilter::filter, InstanceOfAssertFactories.type(GtFilter.class))
          .extracting(VALUE_EXTRACTOR)
          .isEqualTo(1000d);

        assertThat(query.filter().filters().get(3))
          .asInstanceOf(InstanceOfAssertFactories.type(LtFilter.class))
          .extracting(VALUE_EXTRACTOR)
          .isEqualTo(-3.4447);
    }
}
