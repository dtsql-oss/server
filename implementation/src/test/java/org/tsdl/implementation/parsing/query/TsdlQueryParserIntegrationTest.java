package org.tsdl.implementation.parsing.query;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.tsdl.implementation.model.choice.relation.PrecedesOperator;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.connective.AndConnective;
import org.tsdl.implementation.model.event.TsdlEvent;
import org.tsdl.implementation.model.filter.SinglePointFilter;
import org.tsdl.implementation.model.sample.TsdlSample;
import org.tsdl.implementation.model.sample.aggregation.AverageAggregator;
import org.tsdl.implementation.model.sample.aggregation.MaximumAggregator;
import org.tsdl.implementation.model.sample.aggregation.MinimumAggregator;
import org.tsdl.implementation.model.sample.aggregation.SumAggregator;
import org.tsdl.implementation.parsing.enums.ConnectiveIdentifier;
import org.tsdl.implementation.parsing.enums.FilterType;
import org.tsdl.implementation.parsing.exception.DuplicateIdentifierException;
import org.tsdl.implementation.parsing.exception.InvalidReferenceException;
import org.tsdl.implementation.parsing.exception.UnknownIdentifierException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TsdlQueryParserIntegrationTest extends BaseQueryParserTest {
    // TODO AND(gt(s2)) AS mid
    @Test
    void parserIntegration_detectsAllComponentsCorrectly() {
        var queryString = """
          FILTER:
            AND(gt(2), NOT(lt(3.5)))

          WITH SAMPLES:
            avg(_input) AS s1,
            max(_input) AS s2,
            min(_input) AS s3,
            sum(_input) AS s4

          WITH EVENTS:
            AND(lt(3.5)) AS low,
            OR(NOT(gt(7))) AS high

          CHOOSE:
            low precedes high

          YIELD:
            all periods
          """;

        var query = parser.parseQuery(queryString);

        assertThat(query.identifiers())
          .hasSize(6)
          .extracting(TsdlIdentifier::name)
          .containsExactlyInAnyOrder("s1", "s2", "s3", "high", "low", "s4");

        assertThat(query.filter())
          .asInstanceOf(InstanceOfAssertFactories.type(AndConnective.class))
          .extracting(AndConnective::filters)
          .asInstanceOf(InstanceOfAssertFactories.list(SinglePointFilter.class))
          .hasSize(2)
          .satisfies(filterArguments -> {
              assertThat(filterArguments.get(0))
                .isEqualTo(element.getFilter(FilterType.GT, 2.0));

              assertThat(filterArguments.get(1))
                .isEqualTo(element.getNegatedFilter(element.getFilter(FilterType.LT, 3.5)));
          });

        assertThat(query.samples())
          .asInstanceOf(InstanceOfAssertFactories.list(TsdlSample.class))
          .hasSize(4)
          .satisfies(samples -> {
              assertThat(samples.get(0))
                .asInstanceOf(InstanceOfAssertFactories.type(TsdlSample.class))
                .satisfies(s -> {
                    assertThat(s.aggregator()).isInstanceOf(AverageAggregator.class);
                    assertThat(s.identifier().name()).isEqualTo("s1");
                });

              assertThat(samples.get(1))
                .asInstanceOf(InstanceOfAssertFactories.type(TsdlSample.class))
                .satisfies(s -> {
                    assertThat(s.aggregator()).isInstanceOf(MaximumAggregator.class);
                    assertThat(s.identifier().name()).isEqualTo("s2");
                });

              assertThat(samples.get(2))
                .asInstanceOf(InstanceOfAssertFactories.type(TsdlSample.class))
                .satisfies(s -> {
                    assertThat(s.aggregator()).isInstanceOf(MinimumAggregator.class);
                    assertThat(s.identifier().name()).isEqualTo("s3");
                });

              assertThat(samples.get(3))
                .asInstanceOf(InstanceOfAssertFactories.type(TsdlSample.class))
                .satisfies(s -> {
                    assertThat(s.aggregator()).isInstanceOf(SumAggregator.class);
                    assertThat(s.identifier().name()).isEqualTo("s4");
                });
          });

        assertThat(query.events())
          .asInstanceOf(InstanceOfAssertFactories.list(TsdlEvent.class))
          .hasSize(2)
          .satisfies(events -> {
              assertThat(events.get(0))
                .asInstanceOf(InstanceOfAssertFactories.type(TsdlEvent.class))
                .extracting(TsdlEvent::definition, TsdlEvent::identifier)
                .containsExactly(
                  element.getConnective(ConnectiveIdentifier.AND, List.of(element.getFilter(FilterType.LT, 3.5))),
                  element.getIdentifier("low")
                );

              assertThat(events.get(1))
                .asInstanceOf(InstanceOfAssertFactories.type(TsdlEvent.class))
                .extracting(TsdlEvent::definition, TsdlEvent::identifier)
                .containsExactly(
                  element.getConnective(ConnectiveIdentifier.OR, List.of(element.getNegatedFilter(element.getFilter(FilterType.GT, 7.0)))),
                  element.getIdentifier("high")
                );
          });

        var low = query.events().stream().filter(event -> event.identifier().name().equals("low")).findFirst().orElseThrow();
        var high = query.events().stream().filter(event -> event.identifier().name().equals("high")).findFirst().orElseThrow();
        assertThat(query.choice())
          .asInstanceOf(InstanceOfAssertFactories.type(PrecedesOperator.class))
          .extracting(PrecedesOperator::cardinality, PrecedesOperator::operand1, PrecedesOperator::operand2)
          .containsExactly(2, low, high);
    }

    @Test
    void parserIntegration_duplicateIdentifierDeclarationInSameGroup_throws() {
        var queryString = """
          WITH SAMPLES: avg(_input) AS s1, max(_input) AS s1
          YIELD: all periods
          """;

        assertThatThrownBy(() -> parser.parseQuery(queryString)).isInstanceOf(DuplicateIdentifierException.class);
    }

    @Test
    void parserIntegration_duplicateIdentifierDeclarationInSeparateGroup_throws() {
        var queryString = """
          WITH SAMPLES: avg(_input) AS s1
          WITH EVENTS: AND(lt(3.5)) AS s1
          YIELD: all periods
          """;

        assertThatThrownBy(() -> parser.parseQuery(queryString)).isInstanceOf(DuplicateIdentifierException.class);
    }

    @Test
    void parserIntegration_undeclaredIdentifier_throws() {
        var queryString = """
          CHOOSE: low precedes high
          YIELD: all periods
          """;

        assertThatThrownBy(() -> parser.parseQuery(queryString)).isInstanceOf(UnknownIdentifierException.class);
    }

    @Test
    void parserIntegration_invalidIdentifierReference_throws() {
        var queryString = """
          WITH SAMPLES: min(_input) AS low, max(_input) AS high
          CHOOSE: low precedes high
          YIELD: all periods
          """;

        assertThatThrownBy(() -> parser.parseQuery(queryString)).isInstanceOf(InvalidReferenceException.class);
    }
}
