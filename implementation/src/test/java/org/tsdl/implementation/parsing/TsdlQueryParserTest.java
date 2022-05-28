package org.tsdl.implementation.parsing;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.tsdl.implementation.factory.ObjectFactory;
import org.tsdl.implementation.factory.TsdlElementFactory;
import org.tsdl.implementation.model.choice.relation.FollowsOperator;
import org.tsdl.implementation.model.choice.relation.PrecedesOperator;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.connective.AndConnective;
import org.tsdl.implementation.model.connective.OrConnective;
import org.tsdl.implementation.model.event.TsdlEvent;
import org.tsdl.implementation.model.filter.*;
import org.tsdl.implementation.model.filter.argument.TsdlSampleFilterArgument;
import org.tsdl.implementation.model.result.ResultFormat;
import org.tsdl.implementation.model.sample.TsdlSample;
import org.tsdl.implementation.model.sample.aggregation.AverageAggregator;
import org.tsdl.implementation.model.sample.aggregation.MaximumAggregator;
import org.tsdl.implementation.model.sample.aggregation.MinimumAggregator;
import org.tsdl.implementation.model.sample.aggregation.SumAggregator;
import org.tsdl.implementation.parsing.enums.ConnectiveIdentifier;
import org.tsdl.implementation.parsing.enums.FilterType;
import org.tsdl.implementation.parsing.exception.DuplicateIdentifierException;
import org.tsdl.implementation.parsing.exception.InvalidReferenceException;
import org.tsdl.implementation.parsing.exception.TsdlParserException;
import org.tsdl.implementation.parsing.exception.UnknownIdentifierException;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TsdlQueryParserTest {
    private static final TsdlQueryParser PARSER = ObjectFactory.INSTANCE.queryParser();
    private static final TsdlElementFactory ELEMENTS = ObjectFactory.INSTANCE.elementFactory();
    private static final Function<? super ThresholdFilter, Double> VALUE_EXTRACTOR = filter -> filter.threshold().value();

    @Nested
    @DisplayName("sample declaration tests")
    class SampleDeclaration {
        // @MethodSource does not work very well in @Nested class => @ArgumentsSource wih ArgumentsProvider as alternative
        @ParameterizedTest
        @ArgumentsSource(SamplesArgumentsProvider.class)
        void sampleDeclaration_knownAggregatorFunctions(String aggregator, Class<? extends TsdlSample> clazz) {
            var queryString = """
              WITH SAMPLES: %s(_input) AS s1
              YIELD: all periods
              """.formatted(aggregator);

            var query = PARSER.parseQuery(queryString);

            assertThat(query.samples())
              .hasSize(1)
              .element(0, InstanceOfAssertFactories.type(TsdlSample.class))
              .satisfies(sample -> {
                  assertThat(sample.identifier()).isEqualTo(ELEMENTS.getIdentifier("s1"));
                  assertThat(sample.aggregator()).isInstanceOf(clazz);
              });
        }

        @ParameterizedTest
        @ValueSource(strings = {"_", "_s1", "sö", "", "123", "1s"})
        void sampleDeclaration_invalidIdentifier_throws(String identifier) {
            var queryString = """
              WITH SAMPLES: avg(_input) AS %s
              YIELD: all periods
              """.formatted(identifier);

            assertThatThrownBy(() -> PARSER.parseQuery(queryString)).isInstanceOf(TsdlParserException.class);
        }

        static class SamplesArgumentsProvider implements ArgumentsProvider {
            @Override
            public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
                return Stream.of(
                  Arguments.of("avg", AverageAggregator.class),
                  Arguments.of("max", MaximumAggregator.class),
                  Arguments.of("min", MinimumAggregator.class),
                  Arguments.of("sum", SumAggregator.class)
                );
            }
        }
    }

    @Nested
    @DisplayName("filter declaration tests")
    class FilterDeclaration {

        @Test
        void filterDeclaration_conjunctiveFilterWithOneArgument() {
            var queryString = """
              FILTER:
                AND(gt(23.4))
              YIELD: data points
              """;

            var query = PARSER.parseQuery(queryString);

            assertThat(query.filter())
              .asInstanceOf(InstanceOfAssertFactories.type(AndConnective.class))
              .extracting(AndConnective::filters, InstanceOfAssertFactories.list(SinglePointFilter.class))
              .hasSize(1);

            assertThat(query.filter().filters().get(0))
              .asInstanceOf(InstanceOfAssertFactories.type(GreaterThanFilter.class))
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

            var query = PARSER.parseQuery(queryString);

            assertThat(query.filter())
              .asInstanceOf(InstanceOfAssertFactories.type(OrConnective.class))
              .extracting(OrConnective::filters, InstanceOfAssertFactories.list(SinglePointFilter.class))
              .hasSize(1)
              .element(0, InstanceOfAssertFactories.type(LowerThanFilter.class))
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

            var query = PARSER.parseQuery(queryString);

            assertThat(query.filter())
              .asInstanceOf(InstanceOfAssertFactories.type(OrConnective.class))
              .extracting(OrConnective::filters, InstanceOfAssertFactories.list(SinglePointFilter.class))
              .hasSize(1)
              .element(0, InstanceOfAssertFactories.type(NegatedSinglePointFilter.class))
              .extracting(NegatedSinglePointFilter::filter, InstanceOfAssertFactories.type(LowerThanFilter.class))
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

            var query = PARSER.parseQuery(queryString);

            assertThat(query.filter())
              .asInstanceOf(InstanceOfAssertFactories.type(OrConnective.class))
              .extracting(OrConnective::filters, InstanceOfAssertFactories.list(SinglePointFilter.class))
              .hasSize(4);

            assertThat(query.filter().filters().get(0))
              .asInstanceOf(InstanceOfAssertFactories.type(NegatedSinglePointFilter.class))
              .extracting(NegatedSinglePointFilter::filter, InstanceOfAssertFactories.type(LowerThanFilter.class))
              .extracting(VALUE_EXTRACTOR)
              .isEqualTo(25.1);

            assertThat(query.filter().filters().get(1))
              .asInstanceOf(InstanceOfAssertFactories.type(GreaterThanFilter.class))
              .extracting(VALUE_EXTRACTOR)
              .isEqualTo(3.4);

            assertThat(query.filter().filters().get(2))
              .asInstanceOf(InstanceOfAssertFactories.type(NegatedSinglePointFilter.class))
              .extracting(NegatedSinglePointFilter::filter, InstanceOfAssertFactories.type(GreaterThanFilter.class))
              .extracting(VALUE_EXTRACTOR)
              .isEqualTo(1000d);

            assertThat(query.filter().filters().get(3))
              .asInstanceOf(InstanceOfAssertFactories.type(LowerThanFilter.class))
              .extracting(VALUE_EXTRACTOR)
              .isEqualTo(-3.4447);
        }

        @Test
        void filterDeclaration_sampleAsArgument() {
            var queryString = """
              WITH SAMPLES:
                avg(_input) AS average
              FILTER:
                AND(gt(average))
              YIELD: data points
              """;

            var query = PARSER.parseQuery(queryString);

            assertThat(query.filter())
              .asInstanceOf(InstanceOfAssertFactories.type(AndConnective.class))
              .extracting(AndConnective::filters, InstanceOfAssertFactories.list(SinglePointFilter.class))
              .hasSize(1)
              .element(0, InstanceOfAssertFactories.type(GreaterThanFilter.class))
              .extracting(GreaterThanFilter::threshold, InstanceOfAssertFactories.type(TsdlSampleFilterArgument.class))
              .extracting(arg -> arg.sample().identifier().name(), InstanceOfAssertFactories.STRING)
              .isEqualTo("average");
        }
    }

    @Nested
    @DisplayName("event declaration tests")
    class EventDeclaration {
        @Test
        void eventDeclaration_valid() {
            var queryString = """
              USING EVENTS: AND(lt(2)) AS high, OR(gt(-3.2)) AS low
              YIELD: all periods
              """;

            var query = PARSER.parseQuery(queryString);

            assertThat(query.events())
              .hasSize(2)
              .element(0, InstanceOfAssertFactories.type(TsdlEvent.class))
              .extracting(TsdlEvent::identifier, TsdlEvent::definition)
              .containsExactly(
                ELEMENTS.getIdentifier("high"),
                ELEMENTS.getConnective(ConnectiveIdentifier.AND, List.of(ELEMENTS.getFilter(FilterType.LT, ELEMENTS.getFilterArgument(2d))))
              );
        }

        @Test
        void eventDeclaration_validWithSample() {
            var queryString = """
              WITH SAMPLES: avg(_input) AS s3
              USING EVENTS: OR(gt(-3.2)) AS low, AND(lt(s3)) AS sampledHigh
              YIELD: all periods
              """;

            var query = PARSER.parseQuery(queryString);

            assertThat(query.events())
              .hasSize(2)
              .element(1, InstanceOfAssertFactories.type(TsdlEvent.class))
              .satisfies(event -> {
                  assertThat(event.identifier()).isEqualTo(ELEMENTS.getIdentifier("sampledHigh"));

                  assertThat(event.definition().filters().get(0))
                    .asInstanceOf(InstanceOfAssertFactories.type(ThresholdFilter.class))
                    .extracting(ThresholdFilter::threshold, InstanceOfAssertFactories.type(TsdlSampleFilterArgument.class))
                    .extracting(sample -> sample.sample().identifier().name())
                    .isEqualTo("s3");
              });
        }

        @Test
        void eventDeclaration_invalidIdentifier_throws() {
            var queryString = """
              USING EVENTS: AND(lt(2)) AS 1high,
              YIELD: all periods
              """;

            assertThatThrownBy(() -> PARSER.parseQuery(queryString)).isInstanceOf(TsdlParserException.class);
        }

        @Test
        void eventDeclaration_unknownSample_throws() {
            var queryString = """
              USING EVENTS: AND(lt(s3)) AS high
              YIELD: all periods
              """;

            assertThatThrownBy(() -> PARSER.parseQuery(queryString))
              .isInstanceOf(UnknownIdentifierException.class)
              .hasMessageContaining("s3");
        }

        @Test
        void eventDeclaration_invalidSampleReference_throws() {
            Assertions.setMaxStackTraceElementsDisplayed(10);
            var queryString = """
              USING EVENTS: AND(lt(3.5)) AS low, OR(gt(low)) AS high
              CHOOSE: low precedes high
              YIELD: all periods
              """;

            // depending on whether identifier 'low' is parsed before filter argument 'lt(3.5)' or after,
            // an InvalidReferenceException or UnknownIdentifierException is thrown
            assertThatThrownBy(() -> PARSER.parseQuery(queryString))
              .isInstanceOfAny(UnknownIdentifierException.class, InvalidReferenceException.class)
              .hasMessageContaining("low");
        }
    }

    @Nested
    @DisplayName("choose declaration tests")
    class ChooseDeclaration {
        @Test
        void chooseDeclaration_precedes() {
            var queryString = """
              USING EVENTS: AND(lt(3)) AS e1,
                            OR(gt(5)) AS e2
              CHOOSE: e1 precedes e2
              YIELD: data points
              """;

            var query = PARSER.parseQuery(queryString);

            assertThat(query.choice())
              .asInstanceOf(InstanceOfAssertFactories.type(PrecedesOperator.class))
              .satisfies(op -> {
                  assertThat(op.cardinality()).isEqualTo(2);
                  assertThat(op.operand1().identifier().name()).isEqualTo("e1");
                  assertThat(op.operand2().identifier().name()).isEqualTo("e2");
              });
        }

        @Test
        void chooseDeclaration_follows() {
            var queryString = """
              USING EVENTS: AND(lt(3)) AS e1,
                            OR(gt(5)) AS e2
              CHOOSE: e2 follows e1
              YIELD: data points
              """;

            var query = PARSER.parseQuery(queryString);

            assertThat(query.choice())
              .asInstanceOf(InstanceOfAssertFactories.type(FollowsOperator.class))
              .satisfies(op -> {
                  assertThat(op.cardinality()).isEqualTo(2);
                  assertThat(op.operand1().identifier().name()).isEqualTo("e2");
                  assertThat(op.operand2().identifier().name()).isEqualTo("e1");
              });
        }

        @Test
        void chooseDeclaration_unknownEvent_throws() {
            var queryString = """
              USING EVENTS: AND(lt(3)) AS e1
              CHOOSE: e1 follows e2
              YIELD: data points
              """;

            assertThatThrownBy(() -> PARSER.parseQuery(queryString))
              .isInstanceOf(UnknownIdentifierException.class)
              .hasMessageContaining("e2");
        }

        @Test
        void chooseDeclaration_invalidEventReference_throws() {
            var queryString = """
              WITH SAMPLES: min(_input) AS low, max(_input) AS high
              CHOOSE: low precedes high
              YIELD: all periods
              """;

            assertThatThrownBy(() -> PARSER.parseQuery(queryString))
              .isInstanceOf(InvalidReferenceException.class)
              .hasMessageContainingAll("low", "event");
        }

        @Test
        void chooseDeclaration_multipleStatements_throws() {
            var queryString = """
              USING EVENTS: AND(lt(3)) AS e1,
                            OR(gt(5)) AS e2
              CHOOSE: e1 precedes e2, e2 follows e1
              YIELD: data points
              """;

            assertThatThrownBy(() -> PARSER.parseQuery(queryString)).isInstanceOf(TsdlParserException.class);
        }
    }

    @Nested
    @DisplayName("yield declaration tests")
    class YieldDeclaration {
        // @MethodSource does not work very well in @Nested class => @ArgumentsSource wih ArgumentsProvider as alternative
        @ArgumentsSource(ValidInputProvider.class)
        @ParameterizedTest
        void yieldDeclaration_validRepresentations_parsed(String representation, ResultFormat member) {
            var queryString = "YIELD: %s".formatted(representation);
            var query = PARSER.parseQuery(queryString);

            assertThat(query.result()).isEqualTo(member);
        }

        @ParameterizedTest
        @ValueSource(strings = {"ALL periods", "longestperiod", "shortest perioD", "", "      ", "0", "1"})
        void yieldDeclaration_invalidRepresentations_throws(String representation) {
            var queryString = "YIELD: %s".formatted(representation);
            assertThatThrownBy(() -> PARSER.parseQuery(queryString)).isInstanceOf(TsdlParserException.class);
        }

        static class ValidInputProvider implements ArgumentsProvider {
            @Override
            public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
                return Stream.of(
                  Arguments.of("all periods", ResultFormat.ALL_PERIODS),
                  Arguments.of("longest period", ResultFormat.LONGEST_PERIOD),
                  Arguments.of("shortest period", ResultFormat.SHORTEST_PERIOD),
                  Arguments.of("data points", ResultFormat.DATA_POINTS)
                );

            }
        }
    }

    @Nested
    @DisplayName("integration tests")
    class Integration {
        private static final String FULL_FEATURE_QUERY = """
          WITH SAMPLES:
            avg(_input) AS s1,
            max(_input) AS s2,
            min(_input) AS s3,
            sum(_input) AS s4

          FILTER:
            AND(gt(s2), NOT(lt(3.5)))

          USING EVENTS:
            AND(lt(3.5)) AS low,
            OR(NOT(gt(7))) AS high,
            AND(gt(s2)) AS mid

          CHOOSE:
            low precedes high

          YIELD:
            all periods
          """;

        @ValueSource(strings = FULL_FEATURE_QUERY)
        @ParameterizedTest
        void integration_detectsIdentifiers(String queryString) {
            var query = PARSER.parseQuery(queryString);

            assertThat(query.identifiers())
              .hasSize(7)
              .extracting(TsdlIdentifier::name)
              .containsExactlyInAnyOrder("s1", "s2", "s3", "high", "low", "s4", "mid");
        }

        @ValueSource(strings = FULL_FEATURE_QUERY)
        @ParameterizedTest
        void integration_detectsFilters(String queryString) {
            var query = PARSER.parseQuery(queryString);

            assertThat(query.filter())
              .asInstanceOf(InstanceOfAssertFactories.type(AndConnective.class))
              .extracting(AndConnective::filters, InstanceOfAssertFactories.list(SinglePointFilter.class))
              .hasSize(2)
              .satisfies(filterArguments -> {
                  assertThat(filterArguments.get(0))
                    .asInstanceOf(InstanceOfAssertFactories.type(GreaterThanFilter.class))
                    .extracting(GreaterThanFilter::threshold, InstanceOfAssertFactories.type(TsdlSampleFilterArgument.class))
                    .extracting(arg -> arg.sample().identifier().name(), InstanceOfAssertFactories.STRING)
                    .isEqualTo("s2");

                  assertThat(filterArguments.get(1))
                    .isEqualTo(ELEMENTS.getNegatedFilter(ELEMENTS.getFilter(FilterType.LT, ELEMENTS.getFilterArgument(3.5))));
              });
        }

        @ValueSource(strings = FULL_FEATURE_QUERY)
        @ParameterizedTest
        void integration_detectsSamples(String queryString) {
            var query = PARSER.parseQuery(queryString);

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
        }

        @ValueSource(strings = FULL_FEATURE_QUERY)
        @ParameterizedTest
        void integration_detectsEvents(String queryString) {
            var query = PARSER.parseQuery(queryString);

            assertThat(query.events())
              .asInstanceOf(InstanceOfAssertFactories.list(TsdlEvent.class))
              .hasSize(3)
              .satisfies(events -> {
                  assertThat(events.get(0))
                    .asInstanceOf(InstanceOfAssertFactories.type(TsdlEvent.class))
                    .extracting(TsdlEvent::definition, TsdlEvent::identifier)
                    .containsExactly(
                      ELEMENTS.getConnective(ConnectiveIdentifier.AND,
                        List.of(ELEMENTS.getFilter(FilterType.LT, ELEMENTS.getFilterArgument(3.5)))
                      ),
                      ELEMENTS.getIdentifier("low")
                    );

                  assertThat(events.get(1))
                    .asInstanceOf(InstanceOfAssertFactories.type(TsdlEvent.class))
                    .extracting(TsdlEvent::definition, TsdlEvent::identifier)
                    .containsExactly(
                      ELEMENTS.getConnective(ConnectiveIdentifier.OR,
                        List.of(ELEMENTS.getNegatedFilter(ELEMENTS.getFilter(FilterType.GT, ELEMENTS.getFilterArgument(7.0))))
                      ),
                      ELEMENTS.getIdentifier("high")
                    );

                  assertThat(events.get(2))
                    .asInstanceOf(InstanceOfAssertFactories.type(TsdlEvent.class))
                    .satisfies(event -> {
                        assertThat(event.definition().filters())
                          .hasSize(1)
                          .element(0, InstanceOfAssertFactories.type(GreaterThanFilter.class))
                          .extracting(GreaterThanFilter::threshold, InstanceOfAssertFactories.type(TsdlSampleFilterArgument.class))
                          .extracting(arg -> arg.sample().identifier().name(), InstanceOfAssertFactories.STRING)
                          .isEqualTo("s2");

                        assertThat(event.identifier()).isEqualTo(ELEMENTS.getIdentifier("mid"));
                    });
              });
        }

        @ValueSource(strings = FULL_FEATURE_QUERY)
        @ParameterizedTest
        void integration_detectsChoice(String queryString) {
            var query = PARSER.parseQuery(queryString);

            var low = query.events().stream().filter(event -> event.identifier().name().equals("low")).findFirst().orElseThrow();
            var high = query.events().stream().filter(event -> event.identifier().name().equals("high")).findFirst().orElseThrow();
            assertThat(query.choice())
              .asInstanceOf(InstanceOfAssertFactories.type(PrecedesOperator.class))
              .extracting(PrecedesOperator::cardinality, PrecedesOperator::operand1, PrecedesOperator::operand2)
              .containsExactly(2, low, high);
        }

        @Test
        void integration_duplicateIdentifierDeclarationInSameGroup_throws() {
            var queryString = """
              WITH SAMPLES: avg(_input) AS s1, max(_input) AS s1
              YIELD: all periods
              """;

            assertThatThrownBy(() -> PARSER.parseQuery(queryString)).isInstanceOf(DuplicateIdentifierException.class);
        }

        @Test
        void integration_duplicateIdentifierDeclarationInSeparateGroup_throws() {
            var queryString = """
              WITH SAMPLES: avg(_input) AS s1
              USING EVENTS: AND(lt(3.5)) AS s1
              YIELD: all periods
              """;

            assertThatThrownBy(() -> PARSER.parseQuery(queryString)).isInstanceOf(DuplicateIdentifierException.class);
        }
    }
}
