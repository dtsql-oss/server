package org.tsdl.implementation.parsing;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.tsdl.implementation.evaluation.impl.connective.AndConnectiveImpl;
import org.tsdl.implementation.evaluation.impl.connective.OrConnectiveImpl;
import org.tsdl.implementation.evaluation.impl.filter.GtFilterImpl;
import org.tsdl.implementation.evaluation.impl.filter.LtFilterImpl;
import org.tsdl.implementation.factory.ObjectFactory;
import org.tsdl.implementation.model.filter.NegatedSinglePointFilter;
import org.tsdl.implementation.model.filter.SinglePointFilter;

import static org.assertj.core.api.Assertions.assertThat;

public class TsdlQueryParserTest {
    private final TsdlQueryParser parser = ObjectFactory.INSTANCE.getParser();

    @Test
    void tsdlParser_conjunctiveFilterWithOneArgument() {
        var queryString = """
          FILTER:
            AND(gt(23.4))
          YIELD *
          """;

        var query = parser.parseQuery(queryString);

        assertThat(query.filter())
          .asInstanceOf(InstanceOfAssertFactories.type(AndConnectiveImpl.class))
          .extracting(AndConnectiveImpl::filters)
          .asInstanceOf(InstanceOfAssertFactories.list(SinglePointFilter.class))
          .hasSize(1);

        assertThat(query.filter().filters().get(0))
          .asInstanceOf(InstanceOfAssertFactories.type(GtFilterImpl.class))
          .extracting(GtFilterImpl::threshold)
          .isEqualTo(23.4);
    }

    @Test
    void tsdlParser_disjunctiveFilterWithOneArgument() {
        var queryString = """
          FILTER:
            OR(lt(-2.3))
          YIELD *
          """;

        var query = parser.parseQuery(queryString);

        assertThat(query.filter())
          .asInstanceOf(InstanceOfAssertFactories.type(OrConnectiveImpl.class))
          .extracting(OrConnectiveImpl::filters)
          .asInstanceOf(InstanceOfAssertFactories.list(SinglePointFilter.class))
          .hasSize(1);

        assertThat(query.filter().filters().get(0))
          .asInstanceOf(InstanceOfAssertFactories.type(LtFilterImpl.class))
          .extracting(LtFilterImpl::threshold)
          .isEqualTo(-2.3);
    }

    @Test
    void tsdlParser_negatedFilter() {
        var queryString = """
          FILTER:
            OR(NOT(lt(25)))
          YIELD *
          """;

        var query = parser.parseQuery(queryString);

        assertThat(query.filter())
          .asInstanceOf(InstanceOfAssertFactories.type(OrConnectiveImpl.class))
          .extracting(OrConnectiveImpl::filters)
          .asInstanceOf(InstanceOfAssertFactories.list(SinglePointFilter.class))
          .hasSize(1);

        assertThat(query.filter().filters().get(0))
          .asInstanceOf(InstanceOfAssertFactories.type(NegatedSinglePointFilter.class))
          .extracting(NegatedSinglePointFilter::filter)
          .asInstanceOf(InstanceOfAssertFactories.type(LtFilterImpl.class))
          .extracting(LtFilterImpl::threshold)
          .isEqualTo(25d);
    }

    @Test
    void tsdlParser_multipleArguments() {
        var queryString = """
          FILTER:
            OR(
                NOT(lt(25.1)),       gt(3.4),
                NOT(gt(1000)),
                lt(-3.4447)
              )
          YIELD *
          """;

        var query = parser.parseQuery(queryString);

        assertThat(query.filter())
          .asInstanceOf(InstanceOfAssertFactories.type(OrConnectiveImpl.class))
          .extracting(OrConnectiveImpl::filters)
          .asInstanceOf(InstanceOfAssertFactories.list(SinglePointFilter.class))
          .hasSize(4);

        assertThat(query.filter().filters().get(0))
          .asInstanceOf(InstanceOfAssertFactories.type(NegatedSinglePointFilter.class))
          .extracting(NegatedSinglePointFilter::filter)
          .asInstanceOf(InstanceOfAssertFactories.type(LtFilterImpl.class))
          .extracting(LtFilterImpl::threshold)
          .isEqualTo(25.1);

        assertThat(query.filter().filters().get(1))
          .asInstanceOf(InstanceOfAssertFactories.type(GtFilterImpl.class))
          .extracting(GtFilterImpl::threshold)
          .isEqualTo(3.4);

        assertThat(query.filter().filters().get(2))
          .asInstanceOf(InstanceOfAssertFactories.type(NegatedSinglePointFilter.class))
          .extracting(NegatedSinglePointFilter::filter)
          .asInstanceOf(InstanceOfAssertFactories.type(GtFilterImpl.class))
          .extracting(GtFilterImpl::threshold)
          .isEqualTo(1000d);

        assertThat(query.filter().filters().get(3))
          .asInstanceOf(InstanceOfAssertFactories.type(LtFilterImpl.class))
          .extracting(LtFilterImpl::threshold)
          .isEqualTo(-3.4447);
    }
}
