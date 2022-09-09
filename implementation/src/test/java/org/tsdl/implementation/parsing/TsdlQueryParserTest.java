package org.tsdl.implementation.parsing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.THROWABLE;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.tsdl.implementation.evaluation.impl.common.TsdlDurationImpl;
import org.tsdl.implementation.evaluation.impl.common.formatting.TsdlSampleOutputFormatter;
import org.tsdl.implementation.evaluation.impl.sample.aggregation.temporal.TimePeriodImpl;
import org.tsdl.implementation.factory.TsdlComponentFactory;
import org.tsdl.implementation.factory.TsdlQueryElementFactory;
import org.tsdl.implementation.model.choice.relation.FollowsOperator;
import org.tsdl.implementation.model.choice.relation.PrecedesOperator;
import org.tsdl.implementation.model.common.ParsableTsdlTimeUnit;
import org.tsdl.implementation.model.common.TsdlDuration;
import org.tsdl.implementation.model.common.TsdlDurationBound;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.common.TsdlOutputFormatter;
import org.tsdl.implementation.model.connective.AndFilterConnective;
import org.tsdl.implementation.model.connective.OrFilterConnective;
import org.tsdl.implementation.model.event.TsdlEvent;
import org.tsdl.implementation.model.filter.NegatedSinglePointFilter;
import org.tsdl.implementation.model.filter.SinglePointFilter;
import org.tsdl.implementation.model.filter.argument.TsdlSampleScalarArgument;
import org.tsdl.implementation.model.filter.deviation.AbsoluteAroundFilter;
import org.tsdl.implementation.model.filter.deviation.RelativeAroundFilter;
import org.tsdl.implementation.model.filter.temporal.AfterFilter;
import org.tsdl.implementation.model.filter.temporal.BeforeFilter;
import org.tsdl.implementation.model.filter.temporal.TemporalFilter;
import org.tsdl.implementation.model.filter.threshold.GreaterThanFilter;
import org.tsdl.implementation.model.filter.threshold.LessThanFilter;
import org.tsdl.implementation.model.filter.threshold.ThresholdFilter;
import org.tsdl.implementation.model.result.YieldFormat;
import org.tsdl.implementation.model.result.YieldStatement;
import org.tsdl.implementation.model.sample.TsdlSample;
import org.tsdl.implementation.model.sample.aggregation.TsdlAggregator;
import org.tsdl.implementation.model.sample.aggregation.temporal.TemporalAggregator;
import org.tsdl.implementation.model.sample.aggregation.temporal.TemporalAggregatorWithUnit;
import org.tsdl.implementation.model.sample.aggregation.temporal.TemporalAverageAggregator;
import org.tsdl.implementation.model.sample.aggregation.temporal.TemporalMaximumAggregator;
import org.tsdl.implementation.model.sample.aggregation.value.AverageAggregator;
import org.tsdl.implementation.model.sample.aggregation.value.CountAggregator;
import org.tsdl.implementation.model.sample.aggregation.value.IntegralAggregator;
import org.tsdl.implementation.model.sample.aggregation.value.MaximumAggregator;
import org.tsdl.implementation.model.sample.aggregation.value.MinimumAggregator;
import org.tsdl.implementation.model.sample.aggregation.value.StandardDeviationAggregator;
import org.tsdl.implementation.model.sample.aggregation.value.SumAggregator;
import org.tsdl.implementation.model.sample.aggregation.value.ValueAggregator;
import org.tsdl.implementation.parsing.enums.AggregatorType;
import org.tsdl.implementation.parsing.enums.ConnectiveIdentifier;
import org.tsdl.implementation.parsing.enums.TemporalFilterType;
import org.tsdl.implementation.parsing.enums.ThresholdFilterType;
import org.tsdl.implementation.parsing.exception.DuplicateIdentifierException;
import org.tsdl.implementation.parsing.exception.InvalidReferenceException;
import org.tsdl.implementation.parsing.exception.TsdlParseException;
import org.tsdl.implementation.parsing.exception.UnknownIdentifierException;

class TsdlQueryParserTest {
  private static final TsdlQueryParser PARSER = TsdlComponentFactory.INSTANCE.queryParser();
  private static final TsdlQueryElementFactory ELEMENTS = TsdlComponentFactory.INSTANCE.elementFactory();
  private static final Function<? super ThresholdFilter, Double> VALUE_EXTRACTOR = filter -> filter.threshold().value();

  @Nested
  @DisplayName("sample declaration tests")
  class SampleDeclaration {
    @ParameterizedTest
    @MethodSource("sampleDeclarationSamplesArguments")
    void sampleDeclaration_knownAggregatorFunctionsWithoutEcho(String aggregator, Class<? extends TsdlSample> clazz) {
      var queryString = "WITH SAMPLES: %s() AS s1\n          YIELD: all periods".formatted(aggregator);

      var query = PARSER.parseQuery(queryString);

      assertThat(query.samples())
          .hasSize(1)
          .element(0, InstanceOfAssertFactories.type(TsdlSample.class))
          .satisfies(sample -> {
            assertThat(sample.identifier()).isEqualTo(ELEMENTS.getIdentifier("s1"));
            assertThat(sample.aggregator()).isInstanceOf(clazz);
            assertThat(sample.formatter()).isNotPresent();
          });
    }

    @ParameterizedTest
    @MethodSource("sampleDeclarationSamplesArguments")
    void sampleDeclaration_knownAggregatorFunctionsWithEcho(String aggregator, Class<? extends TsdlSample> clazz) {
      var queryString = "WITH SAMPLES: %s() AS s1->echo(9)\n          YIELD: all periods".formatted(aggregator);

      var query = PARSER.parseQuery(queryString);

      assertThat(query.samples())
          .hasSize(1)
          .element(0, InstanceOfAssertFactories.type(TsdlSample.class))
          .satisfies(sample -> {
            assertThat(sample.identifier()).isEqualTo(ELEMENTS.getIdentifier("s1"));
            assertThat(sample.aggregator()).isInstanceOf(clazz);
            assertThat(sample.formatter())
                .asInstanceOf(InstanceOfAssertFactories.optional(TsdlSampleOutputFormatter.class)).get()
                .extracting(TsdlOutputFormatter::args)
                .isEqualTo(new String[] {"9"});
          });
    }

    @ParameterizedTest
    @MethodSource("sampleDeclarationLocalSamplesArguments")
    void sampleDeclaration_knownAggregatorFunctionsWithTimeRange(String aggregator, Instant lower, Instant upper, Class<? extends TsdlSample> clazz) {
      var queryString = """
            WITH SAMPLES: %s AS s1
            YIELD: all periods
          """.formatted(aggregator);

      var query = PARSER.parseQuery(queryString);

      assertThat(query.samples())
          .hasSize(1)
          .element(0, InstanceOfAssertFactories.type(TsdlSample.class))
          .satisfies(sample -> {
            assertThat(sample.identifier()).isEqualTo(ELEMENTS.getIdentifier("s1"));
            assertThat(sample.aggregator()).isInstanceOf(clazz);
            assertThat(sample.formatter()).isNotPresent();
            assertThat(sample.aggregator())
                .asInstanceOf(InstanceOfAssertFactories.type(ValueAggregator.class))
                .extracting(ValueAggregator::lowerBound, ValueAggregator::upperBound)
                .containsExactly(Optional.ofNullable(lower), Optional.ofNullable(upper));
          });
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "avg(2022-08-08T13:04:23.000Z, 2022-08-08T15:04:23.000Z)", // arguments without quotes (")
        "min(\"2022-08-08T13:04:23.000Z\")", // only one argument
        "max(\"2022-08-08T13:04:23.000Z\", \"2022-08-19T23:55:55.000Z\"\", \"2022-08-24T23:59:55.000Z\")", // three arguments
        "sum(\"2022-08-08T13:04:23.000Z\",\"2022-08-19 23:55:55.000Z\")", // second argument has space " " between date and time instead of "T"
        "integral(\"2022-08-19T23:55:55.000Z\", \"2022-08-08T13:04:23.000Z\")", // first argument is before second argument
        "count(\"2022-08-19T23:55:55.000Z\", \"2022-08-19T23:55:55.000Z\")", // first and second argument are equal
        "avg(\"2022-08-08T13:04:23.000Z\" \"2022-08-19T23:55:55.000Z\")", // no comma "," between first and second argument
        "min(\"2022-08-08T13:04:23.000Z\",,\"2022-08-19T23:55:55.000Z\")", // two commas "," between first and second argument
        "stddev(\"2022-08-08T13:04:23.000Z\"\",\"2022-08-19T23:55:55.000Z\")", // superfluous quote (") at end of first argument
        "sum(\"test\",\"2022-08-19T23:55:55.000Z\")", // invalid first argument
    })
    void sampleDeclaration_knownAggregatorFunctionsWithInvalidTimeRange_throws(String aggregator) {
      Assertions.setMaxStackTraceElementsDisplayed(100);
      var queryString = """
            WITH SAMPLES: %s AS s1
            YIELD: all periods
          """.formatted(aggregator);

      assertThatThrownBy(() -> PARSER.parseQuery(queryString)).isInstanceOf(TsdlParseException.class);
    }

    @ParameterizedTest
    @MethodSource("sampleDeclarationSamplesArguments")
    void sampleDeclaration_knownAggregatorFunctionsWithInvalidEchoArgument_throws(String aggregator) {
      var queryString = "WITH SAMPLES: %s() AS s1 -> echo(NaN)\n          YIELD: all periods".formatted(aggregator);

      assertThatThrownBy(() -> PARSER.parseQuery(queryString)).isInstanceOf(TsdlParseException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"_", "_s1", "sö", "", "123", "1s"})
    void sampleDeclaration_invalidIdentifier_throws(String identifier) {
      var queryString = "WITH SAMPLES: avg() AS %s\n          YIELD: all periods".formatted(identifier);

      assertThatThrownBy(() -> PARSER.parseQuery(queryString)).isInstanceOf(TsdlParseException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "avg2(\"2022-08-08T13:04:23.000Z\",\"\"2022-08-19T23:55:55.000Z\"\")", // valid time range, but unknown function
        "counts()", // valid input specification, but unknown function
        "ints()"
    })
    void sampleDeclaration_unknownAggregatorFunction_throws(String aggregator) {
      var queryString = "WITH SAMPLES: %s AS s1 YIELD: data points".formatted(aggregator);

      assertThatThrownBy(() -> PARSER.parseQuery(queryString)).isInstanceOf(TsdlParseException.class);
    }

    @ParameterizedTest
    @MethodSource("sampleDeclarationTemporalSamplesArguments")
    void sampleDeclaration_temporalAggregators(String aggregatorDefinition, TemporalAggregator expected) {
      var queryString = "WITH SAMPLES: %s AS s1 YIELD: data points".formatted(aggregatorDefinition);

      var parsedQuery = PARSER.parseQuery(queryString);

      assertThat(parsedQuery.samples())
          .hasSize(1)
          .element(0, InstanceOfAssertFactories.type(TsdlSample.class))
          .extracting(TsdlSample::aggregator, InstanceOfAssertFactories.type(TemporalAggregator.class))
          .satisfies(aggregator -> {
            assertThat(aggregator.periods()).isEqualTo(expected.periods());
            if (aggregator instanceof TemporalAggregatorWithUnit withUnit && expected instanceof TemporalAggregatorWithUnit expectedWithUnit) {
              assertThat(withUnit.unit()).isEqualTo(expectedWithUnit.unit());
            }
            assertThat(aggregator.type()).isEqualTo(expected.type());
          });
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "avg_t(millis, \"2022-07-03T12:46:03.123Z/2022-07-03T12:45:03.123Z\")", // end before start
        "avg_t(millis, \"2022-07-03T12:45:03.123Z--2022-07-03T12:46:03.123Z\")", // wrong range separator
        "avg_t(milliseconds, \"2022-07-03T12:45:03.123Z/2022-07-03T12:46:03.123Z\")", // wrong unit
        "avg_t(\"2022-07-03T12:45:03.123Z/2022-07-03T12:46:03.123Z\", millis)", // unit at wrong position
        "avg_t(millis, \"07-03T12:45:03.123Z/2022-07-03T12:46:03.123Z\")", // invalid start timestamp
        "avg_t(millis, \"2022-07-03T12:45:03.123Z/12:46:03.123Z\")", // invalid end timestamp
        "avg_t(millis, 2022-07-03T12:45:03.123Z/2022-07-03T12:46:03.123Z)", // missing quotes
        "avg_t(millis, \"2022-07-03T12:45:03.123Z//2022-07-03T12:46:03.123Z\")", // double range separator
        "avg_t(millis)" // no ranges
    })
    void sampleDeclaration_invalidTemporalAggregators(String aggregatorDefinition) {
      var queryString = "WITH SAMPLES: %s AS s1 YIELD: data points".formatted(aggregatorDefinition);

      assertThatThrownBy(() -> PARSER.parseQuery(queryString)).isInstanceOf(TsdlParseException.class);
    }

    private static Stream<Arguments> sampleDeclarationTemporalSamplesArguments() {
      return Stream.of(
          Arguments.of(
              "avg_t(millis, \"2022-07-03T12:45:03.123Z/2022-07-03T12:46:03.123Z\")",
              ELEMENTS.getAggregator(
                  AggregatorType.TEMPORAL_AVERAGE,
                  List.of(new TimePeriodImpl(Instant.parse("2022-07-03T12:45:03.123Z"), Instant.parse("2022-07-03T12:46:03.123Z"))),
                  ParsableTsdlTimeUnit.MILLISECONDS,
                  TsdlComponentFactory.INSTANCE.summaryStatistics()
              )
          ),
          Arguments.of(
              """
                  min_t(seconds,
                            "2022-07-03T12:45:03.123Z/2022-07-03T12:46:03.123Z",
                            "2022-07-03T12:47:03.123Z/2022-07-03T12:48:03.123Z")
                  """,
              ELEMENTS.getAggregator(
                  AggregatorType.TEMPORAL_MINIMUM,
                  List.of(
                      new TimePeriodImpl(Instant.parse("2022-07-03T12:45:03.123Z"), Instant.parse("2022-07-03T12:46:03.123Z")),
                      new TimePeriodImpl(Instant.parse("2022-07-03T12:47:03.123Z"), Instant.parse("2022-07-03T12:48:03.123Z"))
                  ),
                  ParsableTsdlTimeUnit.SECONDS,
                  TsdlComponentFactory.INSTANCE.summaryStatistics()
              )
          ),
          Arguments.of(
              """
                  stddev_t(weeks,
                            "2022-07-03T12:49:03.123Z/2022-07-03T12:50:03.123Z",
                            "2022-07-03T12:51:03.123Z/2022-07-03T12:52:03.123Z",
                            "2022-07-03T12:53:03.123Z/2022-07-03T12:54:03.123Z")
                  """,
              ELEMENTS.getAggregator(
                  AggregatorType.TEMPORAL_STANDARD_DEVIATION,
                  List.of(
                      new TimePeriodImpl(Instant.parse("2022-07-03T12:49:03.123Z"), Instant.parse("2022-07-03T12:50:03.123Z")),
                      new TimePeriodImpl(Instant.parse("2022-07-03T12:51:03.123Z"), Instant.parse("2022-07-03T12:52:03.123Z")),
                      new TimePeriodImpl(Instant.parse("2022-07-03T12:53:03.123Z"), Instant.parse("2022-07-03T12:54:03.123Z"))
                  ),
                  ParsableTsdlTimeUnit.WEEKS,
                  TsdlComponentFactory.INSTANCE.summaryStatistics()
              )
          ),
          Arguments.of(
              """
                  count_t(  "2022-07-03T12:49:03.123Z/2022-07-03T12:50:03.123Z",
                            "2022-07-03T12:51:03.123Z/2022-07-03T12:52:03.123Z",
                            "2022-07-03T12:53:03.123Z/2022-07-03T12:54:03.123Z")
                  """,
              ELEMENTS.getAggregator(
                  AggregatorType.TEMPORAL_COUNT,
                  List.of(
                      new TimePeriodImpl(Instant.parse("2022-07-03T12:49:03.123Z"), Instant.parse("2022-07-03T12:50:03.123Z")),
                      new TimePeriodImpl(Instant.parse("2022-07-03T12:51:03.123Z"), Instant.parse("2022-07-03T12:52:03.123Z")),
                      new TimePeriodImpl(Instant.parse("2022-07-03T12:53:03.123Z"), Instant.parse("2022-07-03T12:54:03.123Z"))
                  ),
                  null,
                  TsdlComponentFactory.INSTANCE.summaryStatistics()
              )
          )
      );
    }

    private static Stream<Arguments> sampleDeclarationSamplesArguments() {
      return Stream.of(
          Arguments.of("avg", AverageAggregator.class),
          Arguments.of("max", MaximumAggregator.class),
          Arguments.of("min", MinimumAggregator.class),
          Arguments.of("sum", SumAggregator.class),
          Arguments.of("count", CountAggregator.class),
          Arguments.of("integral", IntegralAggregator.class),
          Arguments.of("stddev", StandardDeviationAggregator.class)
      );
    }

    private static Stream<Arguments> sampleDeclarationLocalSamplesArguments() {
      return Stream.of(
          Arguments.of(
              "avg(\"2022-08-08T13:04:23.000Z\",\"2022-08-19T23:55:55.000Z\")",
              Instant.parse("2022-08-08T13:04:23.000Z"), Instant.parse("2022-08-19T23:55:55.000Z"), AverageAggregator.class
          ),
          Arguments.of(
              "max(\"2022-08-08T13:04:23.000+01:00\",        \"2022-08-19T23:55:55.000Z\")",
              Instant.parse("2022-08-08T13:04:23.000+01:00"), Instant.parse("2022-08-19T23:55:55.000Z"), MaximumAggregator.class
          ),
          Arguments.of(
              "min(\"2022-08-08T13:04:23.000Z\",\r\"2022-08-19T23:55:55.000Z\")",
              Instant.parse("2022-08-08T13:04:23.000Z"), Instant.parse("2022-08-19T23:55:55.000Z"), MinimumAggregator.class
          ),
          Arguments.of(
              "sum(\"2022-08-08T13:04:23.000Z\" ,\r\n\"2022-08-19T23:55:55.000-08:45\")",
              Instant.parse("2022-08-08T13:04:23.000Z"), Instant.parse("2022-08-19T23:55:55.000-08:45"), SumAggregator.class
          ),
          Arguments.of(
              "count(    \"2022-08-08T13:04:23.000Z\"    , \n \"2022-08-19T23:55:55.000Z\"     )",
              Instant.parse("2022-08-08T13:04:23.000Z"), Instant.parse("2022-08-19T23:55:55.000Z"), CountAggregator.class
          ),
          Arguments.of(
              "integral(    \"2022-08-08T13:04:23.000Z\"\r, \n \"2022-08-19T23:55:55.000Z\"     )",
              Instant.parse("2022-08-08T13:04:23.000Z"), Instant.parse("2022-08-19T23:55:55.000Z"), IntegralAggregator.class
          ),
          Arguments.of(
              "integral(    \"\"\r, \n \"2022-08-19T23:55:55.000Z\"     )",
              null, Instant.parse("2022-08-19T23:55:55.000Z"), IntegralAggregator.class
          ),
          Arguments.of(
              "stddev(\"2022-08-08T13:04:23.000+01:00\",        \"\")",
              Instant.parse("2022-08-08T13:04:23.000+01:00"), null, StandardDeviationAggregator.class
          ),
          Arguments.of(
              "avg(\"\",\"\")",
              null, null, AverageAggregator.class
          )
      );
    }
  }

  @Nested
  @DisplayName("filter declaration tests")
  class FilterDeclaration {

    @Test
    void filterDeclaration_conjunctiveThresholdFilterWithOneArgument() {
      var queryString = """
          APPLY FILTER:
                      AND(gt(23.4))
                    YIELD: data points""";

      var query = PARSER.parseQuery(queryString);

      assertThat(query.filter())
          .asInstanceOf(InstanceOfAssertFactories.optional(AndFilterConnective.class))
          .get()
          .extracting(AndFilterConnective::filters, InstanceOfAssertFactories.list(SinglePointFilter.class))
          .hasSize(1);

      assertThat(query.filter())
          .get()
          .extracting(connective -> connective.filters().get(0), InstanceOfAssertFactories.type(GreaterThanFilter.class))
          .extracting(VALUE_EXTRACTOR)
          .isEqualTo(23.4);
    }

    @Test
    void filterDeclaration_disjunctiveThresholdFilterWithOneArgument() {
      var queryString = """
          APPLY FILTER:
                      OR(lt(-2.3))
                    YIELD: data points""";

      var query = PARSER.parseQuery(queryString);

      assertThat(query.filter())
          .asInstanceOf(InstanceOfAssertFactories.optional(OrFilterConnective.class))
          .get()
          .extracting(OrFilterConnective::filters, InstanceOfAssertFactories.list(SinglePointFilter.class))
          .hasSize(1)
          .element(0, InstanceOfAssertFactories.type(LessThanFilter.class))
          .extracting(VALUE_EXTRACTOR)
          .isEqualTo(-2.3);
    }

    @Test
    void filterDeclaration_conjunctiveRelativeDeviationFilterWithOneArgument() {
      var queryString = """
          APPLY FILTER:
                      AND(around(rel  ,  23,75.45))
                    YIELD: data points""";

      var query = PARSER.parseQuery(queryString);

      assertThat(query.filter())
          .asInstanceOf(InstanceOfAssertFactories.optional(AndFilterConnective.class))
          .get()
          .extracting(AndFilterConnective::filters, InstanceOfAssertFactories.list(SinglePointFilter.class))
          .hasSize(1);

      assertThat(query.filter())
          .get()
          .extracting(connective -> connective.filters().get(0), InstanceOfAssertFactories.type(RelativeAroundFilter.class))
          .extracting(RelativeAroundFilter::referenceValue, RelativeAroundFilter::maximumDeviation)
          .containsExactly(ELEMENTS.getScalarArgument(23.0), ELEMENTS.getScalarArgument(75.45));
    }

    @Test
    void filterDeclaration_disjunctiveNegatedAbsoluteDeviationFilterWithOneArgument() {
      var queryString = """
          APPLY FILTER:
                      OR(NOT(around( abs  ,  23, 2345.3)))
                    YIELD: data points""";

      var query = PARSER.parseQuery(queryString);

      assertThat(query.filter())
          .asInstanceOf(InstanceOfAssertFactories.optional(OrFilterConnective.class))
          .get()
          .extracting(OrFilterConnective::filters, InstanceOfAssertFactories.list(SinglePointFilter.class))
          .hasSize(1)
          .element(0, InstanceOfAssertFactories.type(NegatedSinglePointFilter.class))
          .extracting(NegatedSinglePointFilter::filter, InstanceOfAssertFactories.type(AbsoluteAroundFilter.class))
          .extracting(AbsoluteAroundFilter::referenceValue, AbsoluteAroundFilter::maximumDeviation)
          .containsExactly(ELEMENTS.getScalarArgument(23.0), ELEMENTS.getScalarArgument(2345.3));
    }

    @Test
    void filterDeclaration_conjunctiveTemporalFilterWithOneArgument() {
      var queryString = """
          APPLY FILTER:
                      AND(before("2022-07-13T23:14:35.725Z"))
                    YIELD: data points""";

      var query = PARSER.parseQuery(queryString);

      assertThat(query.filter())
          .asInstanceOf(InstanceOfAssertFactories.optional(AndFilterConnective.class))
          .get()
          .extracting(AndFilterConnective::filters, InstanceOfAssertFactories.list(SinglePointFilter.class))
          .hasSize(1);

      assertThat(query.filter())
          .get()
          .extracting(connective -> connective.filters().get(0), InstanceOfAssertFactories.type(BeforeFilter.class))
          .extracting(TemporalFilter::argument)
          .isEqualTo(Instant.parse("2022-07-13T23:14:35.725Z"));
    }

    @Test
    void filterDeclaration_disjunctiveTemporalFilterWithOneArgument() {
      var queryString = """
          APPLY FILTER:
                      OR(after("2022-07-18T05:59:22.237581+03:45"))
                    YIELD: data points""";

      var query = PARSER.parseQuery(queryString);

      assertThat(query.filter())
          .asInstanceOf(InstanceOfAssertFactories.optional(OrFilterConnective.class))
          .get()
          .extracting(OrFilterConnective::filters, InstanceOfAssertFactories.list(SinglePointFilter.class))
          .hasSize(1)
          .element(0, InstanceOfAssertFactories.type(AfterFilter.class))
          .extracting(TemporalFilter::argument)
          .isEqualTo(Instant.parse("2022-07-18T05:59:22.237581+03:45"));
    }

    @Test
    void filterDeclaration_negatedThresholdFilter() {
      var queryString = """
          APPLY FILTER:
                      OR(NOT(lt(25)))
                    YIELD: data points""";

      var query = PARSER.parseQuery(queryString);

      assertThat(query.filter())
          .asInstanceOf(InstanceOfAssertFactories.optional(OrFilterConnective.class))
          .get()
          .extracting(OrFilterConnective::filters, InstanceOfAssertFactories.list(SinglePointFilter.class))
          .hasSize(1)
          .element(0, InstanceOfAssertFactories.type(NegatedSinglePointFilter.class))
          .extracting(NegatedSinglePointFilter::filter, InstanceOfAssertFactories.type(LessThanFilter.class))
          .extracting(VALUE_EXTRACTOR)
          .isEqualTo(25d);
    }

    @Test
    void filterDeclaration_negatedTemporalFilter() {
      var queryString = """
          APPLY FILTER:
                      OR(NOT(before("2022-07-13T23:14:35.725-01:00")))
                    YIELD: data points""";

      var query = PARSER.parseQuery(queryString);

      assertThat(query.filter())
          .asInstanceOf(InstanceOfAssertFactories.optional(OrFilterConnective.class))
          .get()
          .extracting(OrFilterConnective::filters, InstanceOfAssertFactories.list(SinglePointFilter.class))
          .hasSize(1)
          .element(0, InstanceOfAssertFactories.type(NegatedSinglePointFilter.class))
          .extracting(NegatedSinglePointFilter::filter, InstanceOfAssertFactories.type(BeforeFilter.class))
          .extracting(TemporalFilter::argument)
          .isEqualTo(Instant.parse("2022-07-13T23:14:35.725-01:00"));
    }

    @Test
    void filterDeclaration_multipleArguments() {
      var queryString = """
          APPLY FILTER:
                      OR(
                          NOT(lt(25.1)),       gt(3.4),
                          NOT(after("2022-07-13T23:14:35Z")),
                          lt(-3.4447)
                        )
                    YIELD: data points""";

      var query = PARSER.parseQuery(queryString);

      assertThat(query.filter())
          .asInstanceOf(InstanceOfAssertFactories.optional(OrFilterConnective.class))
          .get()
          .extracting(OrFilterConnective::filters, InstanceOfAssertFactories.list(SinglePointFilter.class))
          .hasSize(4);

      assertThat(query.filter())
          .get()
          .extracting(connective -> connective.filters().get(0), InstanceOfAssertFactories.type(NegatedSinglePointFilter.class))
          .extracting(NegatedSinglePointFilter::filter, InstanceOfAssertFactories.type(LessThanFilter.class))
          .extracting(VALUE_EXTRACTOR)
          .isEqualTo(25.1);

      assertThat(query.filter())
          .get()
          .extracting(connective -> connective.filters().get(1), InstanceOfAssertFactories.type(GreaterThanFilter.class))
          .extracting(VALUE_EXTRACTOR)
          .isEqualTo(3.4);

      assertThat(query.filter())
          .get()
          .extracting(connective -> connective.filters().get(2), InstanceOfAssertFactories.type(NegatedSinglePointFilter.class))
          .extracting(NegatedSinglePointFilter::filter, InstanceOfAssertFactories.type(AfterFilter.class))
          .extracting(TemporalFilter::argument)
          .isEqualTo(Instant.parse("2022-07-13T23:14:35Z"));

      assertThat(query.filter())
          .get()
          .extracting(connective -> connective.filters().get(3), InstanceOfAssertFactories.type(LessThanFilter.class))
          .extracting(VALUE_EXTRACTOR)
          .isEqualTo(-3.4447);
    }

    @Test
    void filterDeclaration_sampleAsArgument() {
      var queryString = """
          WITH SAMPLES:
                      avg() AS average
                    APPLY FILTER:
                      AND(gt(average))
                    YIELD: data points""";

      var query = PARSER.parseQuery(queryString);

      assertThat(query.filter())
          .asInstanceOf(InstanceOfAssertFactories.optional(AndFilterConnective.class))
          .get()
          .extracting(AndFilterConnective::filters, InstanceOfAssertFactories.list(SinglePointFilter.class))
          .hasSize(1)
          .element(0, InstanceOfAssertFactories.type(GreaterThanFilter.class))
          .extracting(GreaterThanFilter::threshold, InstanceOfAssertFactories.type(TsdlSampleScalarArgument.class))
          .extracting(arg -> arg.sample().identifier().name(), InstanceOfAssertFactories.STRING)
          .isEqualTo("average");
    }

    @Test
    void filterDeclaration_deviationFilterWithSamplesAsArguments() {
      var queryString = """
          WITH SAMPLES:
                      avg() AS average,
                      stddev() AS standardDeviation
                    APPLY FILTER:
                      AND(around(abs, average, standardDeviation))
                    YIELD: data points""";

      var query = PARSER.parseQuery(queryString);

      assertThat(query.filter())
          .asInstanceOf(InstanceOfAssertFactories.optional(AndFilterConnective.class))
          .get()
          .extracting(AndFilterConnective::filters, InstanceOfAssertFactories.list(SinglePointFilter.class))
          .hasSize(1)
          .element(0, InstanceOfAssertFactories.type(AbsoluteAroundFilter.class))
          .extracting(
              absoluteAroundFilter -> ((TsdlSampleScalarArgument) absoluteAroundFilter.referenceValue()).sample().identifier().name(),
              absoluteAroundFilter -> ((TsdlSampleScalarArgument) absoluteAroundFilter.referenceValue()).sample().aggregator().type(),
              absoluteAroundFilter -> ((TsdlSampleScalarArgument) absoluteAroundFilter.maximumDeviation()).sample().identifier().name(),
              absoluteAroundFilter -> ((TsdlSampleScalarArgument) absoluteAroundFilter.maximumDeviation()).sample().aggregator().type()
          )
          .containsExactly("average", AggregatorType.AVERAGE, "standardDeviation", AggregatorType.STANDARD_DEVIATION);
    }

    @Test
    void filterDeclaration_invalidDeviationFilterRange_throws() {
      var queryString = """
          APPLY FILTER:
            AND(around(abs, 23, -0.25))
          YIELD: data points""";
      assertThatThrownBy(() -> PARSER.parseQuery(queryString))
          .isInstanceOf(TsdlParseException.class)
          .hasMessageContaining("absolute value");
    }

    @ParameterizedTest
    @CsvSource({
        "absolute,25,-0.1",
        "rel,test,-0.1",
        "rel,23,invalid"
    })
    void filterDeclaration_invalidDeviationArguments_throws(String type, String reference, String deviation) {
      var queryString = """
          APPLY FILTER:
            AND(around(%s, %s, %s))
          YIELD: data points""";
      assertThatThrownBy(() -> PARSER.parseQuery(queryString)).isInstanceOf(TsdlParseException.class);
    }
  }

  @Nested
  @DisplayName("event declaration tests")
  class EventDeclaration {
    @Test
    void eventDeclaration_valid() {
      var queryString = "USING EVENTS: AND(lt(2)) AS high, OR(gt(-3.2)) AS low\n"
          + "          YIELD: all periods";

      var query = PARSER.parseQuery(queryString);

      assertThat(query.events())
          .hasSize(2)
          .element(0, InstanceOfAssertFactories.type(TsdlEvent.class))
          .extracting(TsdlEvent::identifier, TsdlEvent::connective)
          .containsExactly(
              ELEMENTS.getIdentifier("high"),
              ELEMENTS.getEventConnective(
                  ConnectiveIdentifier.AND,
                  List.of(ELEMENTS.getThresholdFilter(ThresholdFilterType.LT, ELEMENTS.getScalarArgument(2d)))
              )
          );
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "AND(const(3.4, 12.4))",
        "OR(increase(2.3, 4.2, 12.3)) FOR [,] hours",
        "OR(decrease(2.3, -, 12.3)) FOR (6,] weeks",
        "AND(NOT(const(3.4, 12.4)), NOT(decrease(2.3, -, 12.3)), NOT(increase(2.3, -, 12.3))) FOR [3,4) minutes",
        "AND(const(3.4, 12.4), NOT(decrease(2.3, -, 12.3)), gt(23.4), NOT(lt(50.02)))"
    })
    void eventDeclaration_validComplex(String eventDefinition) {
      var queryString = "USING EVENTS: %s AS myEvent  YIELD: data points".formatted(eventDefinition);

      var query = PARSER.parseQuery(queryString);
      System.out.print("");
      // TODO add assertions
    }

    @Test
    void eventDeclaration_validWithSample() {
      var queryString = """
          WITH SAMPLES: avg() AS s3
                    USING EVENTS: OR(gt(-3.2)) AS low, AND(lt(s3)) AS sampledHigh
                    YIELD: all periods""";

      var query = PARSER.parseQuery(queryString);

      assertThat(query.events())
          .hasSize(2)
          .element(1, InstanceOfAssertFactories.type(TsdlEvent.class))
          .satisfies(event -> {
            assertThat(event.identifier()).isEqualTo(ELEMENTS.getIdentifier("sampledHigh"));

            assertThat(event)
                .extracting(ev -> ev.connective().events().get(0), InstanceOfAssertFactories.type(ThresholdFilter.class))
                .extracting(ThresholdFilter::threshold, InstanceOfAssertFactories.type(TsdlSampleScalarArgument.class))
                .extracting(sample -> sample.sample().identifier().name())
                .isEqualTo("s3");
          });
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "AND(const(s1, 12.4))",
        "OR(increase(2.3, s1, 12.3))",
        "OR(decrease(2.3, -, s1))",
        "AND(NOT(const(3.4, 12.4)), NOT(decrease(s1, -, 12.3)), NOT(increase(s2, -, 12.3)))",
        "AND(const(s1, 12.4), NOT(decrease(s2, s3, 12.3)), gt(s2), NOT(lt(50.02)))"
    })
    void eventDeclaration_validWithSampleComplex(String eventDefinition) {
      var queryString = ("WITH SAMPLES: avg() AS s1, avg()  AS s2, avg() AS s3"
          + " USING EVENTS: %s AS myEvent  YIELD: data points").formatted(eventDefinition);

      var query = PARSER.parseQuery(queryString);
      System.out.print("");
      // TODO add assertions
    }

    @Test
    void eventDeclaration_invalidIdentifier_throws() {
      var queryString = "USING EVENTS: AND(lt(2)) AS 1high,\n"
          + "          YIELD: all periods";

      assertThatThrownBy(() -> PARSER.parseQuery(queryString)).isInstanceOf(TsdlParseException.class);
    }

    @Test
    void eventDeclaration_unknownSample_throws() {
      var queryString = "USING EVENTS: AND(lt(s3)) AS high\n"
          + "          YIELD: all periods";

      assertThatThrownBy(() -> PARSER.parseQuery(queryString))
          .isInstanceOf(TsdlParseException.class)
          .hasCauseInstanceOf(UnknownIdentifierException.class)
          .extracting(Throwable::getCause, THROWABLE)
          .hasMessageContaining("s3");
    }

    @Test
    void eventDeclaration_invalidSampleReference_throws() {
      Assertions.setMaxStackTraceElementsDisplayed(10);
      var queryString = """
          USING EVENTS: AND(lt(3.5)) AS low, OR(gt(low)) AS high
                    CHOOSE: (low precedes high)
                    YIELD: all periods""";

      // depending on whether identifier 'low' is parsed before filter argument 'lt(3.5)' or after,
      // an InvalidReferenceException or UnknownIdentifierException is thrown
      assertThatThrownBy(() -> PARSER.parseQuery(queryString))
          .isInstanceOf(TsdlParseException.class)
          .extracting(Throwable::getCause, InstanceOfAssertFactories.THROWABLE)
          .isInstanceOfAny(UnknownIdentifierException.class, InvalidReferenceException.class)
          .hasMessageContaining("low");
    }

    @ParameterizedTest
    @MethodSource("validEventDurationDeclarationTest")
    void eventDeclaration_withValidDuration(String duration, TsdlDuration expected) {
      var queryString = "USING EVENTS: AND(gt(0)) FOR %s AS e1  YIELD: all periods".formatted(duration);

      var query = PARSER.parseQuery(queryString);

      assertThat(query.events())
          .hasSize(1)
          .element(0, InstanceOfAssertFactories.type(TsdlEvent.class))
          .isEqualTo(ELEMENTS.getEvent(
              ELEMENTS.getEventConnective(ConnectiveIdentifier.AND,
                  List.of(ELEMENTS.getThresholdFilter(ThresholdFilterType.GT, ELEMENTS.getScalarArgument(0d)))),
              ELEMENTS.getIdentifier("e1"),
              expected
          ))
          /*.extracting(TsdlEvent::computationStrategy, InstanceOfAssertFactories.type(TsdlEventStrategyType.class))
          .isEqualTo(TsdlEventStrategyType.SINGLE_POINT_EVENT_WITH_DURATION)*/; // TODO how to assert duration strategy?
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "[11,1) days", "[1,1.25) seconds", "(1 , 1)   hours", "[9283, 9283)   minutes", "[1,1[ weeks", "],0] days", "(9,9) weeks", "[2,2) millis",
        "(3,3] hours", "", "(9283, 9283]   minutes", "  [ -24, 23 ) days", "  [ -24, -23 ) weeks", "  {20,2} hours", "[1, fail) days",
        "(2.4,3) minutes", "[3, 23.5] seconds"
    })
    void eventDeclaration_withInvalidDuration(String duration) {
      var queryString = "USING EVENTS: AND(gt(0)) FOR %s AS e1  YIELD: all periods".formatted(duration);
      assertThatThrownBy(() -> PARSER.parseQuery(queryString)).isInstanceOf(TsdlParseException.class);
    }

    private static Stream<Arguments> validEventDurationDeclarationTest() {
      Function<Object[], TsdlDuration> creator = obj -> ELEMENTS.getDuration(
          TsdlDurationBound.of((long) obj[0], (boolean) obj[1]),
          TsdlDurationBound.of((long) obj[2], (boolean) obj[3]),
          (ParsableTsdlTimeUnit) obj[4]
      );

      return Stream.of(
          Arguments.of("[1,2) days", creator.apply(new Object[] {1L, true, 2L, false, ParsableTsdlTimeUnit.DAYS})),
          Arguments.of("[ 500000  ,] millis", creator.apply(new Object[] {500000L, true, Long.MAX_VALUE, true, ParsableTsdlTimeUnit.MILLISECONDS})),
          Arguments.of("[1,1] seconds", creator.apply(new Object[] {1L, true, 1L, true, ParsableTsdlTimeUnit.SECONDS})),
          Arguments.of("   [,] days", creator.apply(new Object[] {0L, true, Long.MAX_VALUE, true, ParsableTsdlTimeUnit.DAYS})),
          Arguments.of("   (,) days", creator.apply(new Object[] {0L, false, Long.MAX_VALUE, false, ParsableTsdlTimeUnit.DAYS})),
          Arguments.of("\t(,23] weeks", creator.apply(new Object[] {0L, false, 23L, true, ParsableTsdlTimeUnit.WEEKS})),
          Arguments.of("(1 , 2)   minutes", creator.apply(new Object[] {1L, false, 2L, false, ParsableTsdlTimeUnit.MINUTES})),
          Arguments.of("\n   [2\t, \r132)   hours", creator.apply(new Object[] {2L, true, 132L, false, ParsableTsdlTimeUnit.HOURS}))
      );
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
                    CHOOSE: (e1 precedes e2)
                    YIELD: data points""";

      var query = PARSER.parseQuery(queryString);

      assertThat(query.choice())
          .asInstanceOf(InstanceOfAssertFactories.optional(PrecedesOperator.class))
          .get()
          .satisfies(op -> {
            assertThat(op.cardinality()).isEqualTo(2);
            assertThat(op.operand1())
                .asInstanceOf(InstanceOfAssertFactories.type(TsdlEvent.class))
                .extracting(event -> event.identifier().name(), InstanceOfAssertFactories.STRING)
                .isEqualTo("e1");
            assertThat(op.operand2())
                .asInstanceOf(InstanceOfAssertFactories.type(TsdlEvent.class))
                .extracting(event -> event.identifier().name(), InstanceOfAssertFactories.STRING)
                .isEqualTo("e2");
            assertThat(op.tolerance()).isEmpty();
          });
    }

    @ParameterizedTest
    @MethodSource("inputTolerances")
    void chooseDeclaration_precedesWithTolerance(String toleranceSpec, TsdlDuration tolerance) {
      var queryString = """
          USING EVENTS: AND(lt(3)) AS e1,
                                  OR(gt(5)) AS e2
                    CHOOSE: (e1 precedes e2 WITHIN %s)
                    YIELD: data points""".formatted(toleranceSpec);

      var query = PARSER.parseQuery(queryString);

      assertThat(query.choice())
          .asInstanceOf(InstanceOfAssertFactories.optional(PrecedesOperator.class))
          .get()
          .satisfies(op -> {
            assertThat(op.cardinality()).isEqualTo(2);
            assertThat(op.operand1())
                .asInstanceOf(InstanceOfAssertFactories.type(TsdlEvent.class))
                .extracting(event -> event.identifier().name(), InstanceOfAssertFactories.STRING)
                .isEqualTo("e1");
            assertThat(op.operand2())
                .asInstanceOf(InstanceOfAssertFactories.type(TsdlEvent.class))
                .extracting(event -> event.identifier().name(), InstanceOfAssertFactories.STRING)
                .isEqualTo("e2");
            assertThat(op.tolerance())
                .get()
                .isEqualTo(tolerance);
          });
    }

    @Test
    void chooseDeclaration_follows() {
      var queryString = """
          USING EVENTS: AND(lt(3)) AS e1,
                                  OR(gt(5)) AS e2
                    CHOOSE: (e2 follows e1)
                    YIELD: data points""";

      var query = PARSER.parseQuery(queryString);

      assertThat(query.choice())
          .asInstanceOf(InstanceOfAssertFactories.optional(FollowsOperator.class))
          .get()
          .satisfies(op -> {
            assertThat(op.cardinality()).isEqualTo(2);
            assertThat(op.operand1())
                .asInstanceOf(InstanceOfAssertFactories.type(TsdlEvent.class))
                .extracting(event -> event.identifier().name(), InstanceOfAssertFactories.STRING)
                .isEqualTo("e2");
            assertThat(op.operand2())
                .asInstanceOf(InstanceOfAssertFactories.type(TsdlEvent.class))
                .extracting(event -> event.identifier().name(), InstanceOfAssertFactories.STRING)
                .isEqualTo("e1");
            assertThat(op.tolerance()).isEmpty();
          });
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "(e1 follows e2)",
        "(e2 precedes e1 WITHIN [0,23] minutes)",
        "(e1 follows (e2 precedes e1) WITHIN [6,7] hours)",
        "(e4 follows (e3 precedes e6 WITHIN (2,3) minutes) WITHIN (34,87] millis)",
        "((e1 follows e2 WITHIN (2,3] minutes) precedes (e3 precedes (e4 follows e5) WITHIN [1,2) hours) WITHIN [,] weeks)"
    })
    void chooseDeclaration_relationOperators(String choiceDeclaration) {
      var queryString = """
          USING EVENTS:
            AND(lt(1)) AS e1,
            OR(gt(1)) AS e2,
            AND(lt(2)) AS e3,
            OR(lt(2)) AS e4,
            AND(lt(3)) AS e5,
            OR(lt(3)) AS e6
          CHOOSE:
            %s
          YIELD:
            data points
          """.formatted(choiceDeclaration);

      var query = PARSER.parseQuery(queryString);
      System.out.print("");
      // TODO add assertions
    }

    @ParameterizedTest
    @MethodSource("inputTolerances")
    void chooseDeclaration_followsWithTolerance(String toleranceSpec, TsdlDuration tolerance) {
      var queryString = """
          USING EVENTS: AND(lt(3)) AS e1,
                                  OR(gt(5)) AS e2
                    CHOOSE: (e2 follows e1 WITHIN %s)
                    YIELD: data points""".formatted(toleranceSpec);

      var query = PARSER.parseQuery(queryString);

      assertThat(query.choice())
          .asInstanceOf(InstanceOfAssertFactories.optional(FollowsOperator.class))
          .get()
          .satisfies(op -> {
            assertThat(op.cardinality()).isEqualTo(2);
            assertThat(op.operand1())
                .asInstanceOf(InstanceOfAssertFactories.type(TsdlEvent.class))
                .extracting(event -> event.identifier().name(), InstanceOfAssertFactories.STRING)
                .isEqualTo("e2");
            assertThat(op.operand2())
                .asInstanceOf(InstanceOfAssertFactories.type(TsdlEvent.class))
                .extracting(event -> event.identifier().name(), InstanceOfAssertFactories.STRING)
                .isEqualTo("e1");
            assertThat(op.tolerance())
                .get()
                .isEqualTo(tolerance);
          });
    }

    @Test
    void chooseDeclaration_unknownEvent_throws() {
      var queryString = """
          USING EVENTS: AND(lt(3)) AS e1
                    CHOOSE: (e1 follows e2)
                    YIELD: data points""";

      assertThatThrownBy(() -> PARSER.parseQuery(queryString))
          .isInstanceOf(TsdlParseException.class)
          .hasCauseInstanceOf(UnknownIdentifierException.class)
          .extracting(Throwable::getCause, THROWABLE)
          .hasMessageContaining("e2");
    }

    @Test
    void chooseDeclaration_invalidEventReference_throws() {
      var queryString = """
          WITH SAMPLES: min() AS low, max() AS high
                    CHOOSE: (low precedes high)
                    YIELD: all periods""";

      assertThatThrownBy(() -> PARSER.parseQuery(queryString))
          .isInstanceOf(TsdlParseException.class)
          .hasCauseInstanceOf(InvalidReferenceException.class)
          .extracting(Throwable::getCause, InstanceOfAssertFactories.THROWABLE)
          .hasMessageContainingAll("low", "event");
    }

    @Test
    void chooseDeclaration_multipleStatements_throws() {
      var queryString = """
          USING EVENTS: AND(lt(3)) AS e1,
                                  OR(gt(5)) AS e2
                    CHOOSE: e1 precedes e2, e2 follows e1
                    YIELD: data points""";

      assertThatThrownBy(() -> PARSER.parseQuery(queryString)).isInstanceOf(TsdlParseException.class);
    }

    private static Stream<Arguments> inputTolerances() {
      return Stream.of(
          Arguments.of(
              "[3,] weeks",
              ELEMENTS.getDuration(
                  TsdlDurationBound.of(3, true),
                  TsdlDurationBound.of(Long.MAX_VALUE, true),
                  ParsableTsdlTimeUnit.WEEKS
              )
          ),
          Arguments.of(
              "[,) millis",
              ELEMENTS.getDuration(
                  TsdlDurationBound.of(0, true),
                  TsdlDurationBound.of(Long.MAX_VALUE, false),
                  ParsableTsdlTimeUnit.MILLISECONDS
              )
          ),
          Arguments.of(
              "(24,30] hours",
              ELEMENTS.getDuration(
                  TsdlDurationBound.of(24, false),
                  TsdlDurationBound.of(30, true),
                  ParsableTsdlTimeUnit.HOURS
              )
          ),
          Arguments.of(
              "(0,) days",
              ELEMENTS.getDuration(
                  TsdlDurationBound.of(0, false),
                  TsdlDurationBound.of(Long.MAX_VALUE, false),
                  ParsableTsdlTimeUnit.DAYS
              )
          ),
          Arguments.of(
              "(,345] minutes",
              ELEMENTS.getDuration(
                  TsdlDurationBound.of(0, false),
                  TsdlDurationBound.of(345, true),
                  ParsableTsdlTimeUnit.MINUTES
              )
          )
      );
    }
  }

  @Nested
  @DisplayName("yield declaration tests")
  class YieldDeclaration {
    @ParameterizedTest
    @MethodSource("yieldDeclarationValidInput")
    void yieldDeclaration_validRepresentations_parsed(String queryString, YieldStatement result) {
      var query = PARSER.parseQuery(queryString);

      assertThat(query.result()).isEqualTo(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "ALL periods", "longestperiod", "shortest perioD", "", "      ", "0", "1",
        "sample ", "sample", "sample   ", "sample 123", "sample 1up"
    })
    void yieldDeclaration_invalidRepresentations_throws(String representation) {
      var queryString = "YIELD: %s".formatted(representation);
      assertThatThrownBy(() -> PARSER.parseQuery(queryString)).isInstanceOf(TsdlParseException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "YIELD: sample hello1",
        "YIELD: samples hello1",
        "WITH SAMPLES: sum() AS mySum, max() AS myMax YIELD: samples mySum, maxi",
    })
    void yieldDeclaration_unknownSamples_throws(String queryString) {
      assertThatThrownBy(() -> PARSER.parseQuery(queryString))
          .isInstanceOf(TsdlParseException.class)
          .hasCauseInstanceOf(UnknownIdentifierException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "WITH SAMPLES: sum() AS mySum  USING EVENTS: AND(lt(mySum)) AS LOW  YIELD: sample LOW",
        "WITH SAMPLES: sum() AS mySum USING EVENTS: AND(lt(mySum)) AS LOW  YIELD: samples mySum, LOW"
    })
    void yieldDeclaration_invalidSampleTypes_throws(String queryString) {
      assertThatThrownBy(() -> PARSER.parseQuery(queryString))
          .isInstanceOf(TsdlParseException.class)
          .hasCauseInstanceOf(InvalidReferenceException.class);
    }

    private static Stream<Arguments> yieldDeclarationValidInput() {
      return Stream.of(
          Arguments.of("YIELD: all periods", ELEMENTS.getResult(YieldFormat.ALL_PERIODS, null)),
          Arguments.of("YIELD: longest period", ELEMENTS.getResult(YieldFormat.LONGEST_PERIOD, null)),
          Arguments.of("YIELD: shortest period", ELEMENTS.getResult(YieldFormat.SHORTEST_PERIOD, null)),
          Arguments.of("YIELD: data points", ELEMENTS.getResult(YieldFormat.DATA_POINTS, null)),
          Arguments.of("WITH SAMPLES: min() AS identifier1 YIELD: sample identifier1",
              ELEMENTS.getResult(YieldFormat.SAMPLE, List.of(ELEMENTS.getIdentifier("identifier1")))),
          Arguments.of("WITH SAMPLES: min() AS identifier1 YIELD: samples identifier1",
              ELEMENTS.getResult(YieldFormat.SAMPLE_SET, List.of(ELEMENTS.getIdentifier("identifier1")))),
          Arguments.of("WITH SAMPLES: min() AS identifier1, max() AS identifier2 YIELD: samples identifier1, identifier2",
              ELEMENTS.getResult(YieldFormat.SAMPLE_SET, List.of(ELEMENTS.getIdentifier("identifier1"), ELEMENTS.getIdentifier("identifier2")))),
          Arguments.of("WITH SAMPLES: min() AS i1, max() AS i2, count() AS i3 YIELD: samples i1, i2, i3",
              ELEMENTS.getResult(YieldFormat.SAMPLE_SET,
                  List.of(ELEMENTS.getIdentifier("i1"), ELEMENTS.getIdentifier("i2"), ELEMENTS.getIdentifier("i3"))))
      );
    }
  }

  @Nested
  @DisplayName("integration tests")
  class Integration {
    private static final String FULL_FEATURE_QUERY = """
        WITH SAMPLES:
          avg() AS s1,
          max("","") AS s2,
          min("2022-04-03T12:45:03.123Z","") AS s3,
          sum("","2022-07-03T12:45:03.123Z") AS s4,
          count("2022-04-03T12:45:03.123Z", "2022-07-03T12:45:03.123Z") AS s5,
          integral("2022-04-03T12:45:03.123Z", "2022-07-03T12:45:03.123Z") AS s6,
          stddev() AS s7,
          avg_t(millis, "2022-07-03T12:45:03.123Z/2022-07-03T12:46:03.123Z") AS s8,
          max_t(weeks,
                "2022-07-03T12:49:03.123Z/2022-07-03T12:50:03.123Z",
                "2022-07-03T12:51:03.123Z/2022-07-03T12:52:03.123Z",
                "2022-07-03T12:53:03.123Z/2022-07-03T12:54:03.123Z") AS s9

        APPLY FILTER:
          AND(
               gt(s2),
               NOT(lt(3.5)),
               NOT(before("2022-07-03T12:45:03.123Z")),
               after("2022-07-03T12:45:03.123Z"),
               NOT(around(rel, 20, 34)),
               around(abs, s1, s7)
             )

        USING EVENTS:
          AND(lt(3.5)) FOR (3,] weeks  AS low,
          OR(NOT(gt(7))) AS high,
          AND(gt(s2)) AS mid

        CHOOSE:
          (low precedes high WITHIN [23,26] minutes)

        YIELD:
          all periods""";

    @ValueSource(strings = FULL_FEATURE_QUERY)
    @ParameterizedTest
    void integration_detectsIdentifiers(String queryString) {
      var query = PARSER.parseQuery(queryString);

      assertThat(query.identifiers())
          .hasSize(12)
          .extracting(TsdlIdentifier::name)
          .containsExactlyInAnyOrder("s1", "s2", "s3", "s8", "s9", "high", "s7", "low", "s4", "mid", "s5", "s6");
    }

    @ValueSource(strings = FULL_FEATURE_QUERY)
    @ParameterizedTest
    void integration_detectsFilters(String queryString) {
      var query = PARSER.parseQuery(queryString);

      assertThat(query.filter())
          .asInstanceOf(InstanceOfAssertFactories.optional(AndFilterConnective.class))
          .get()
          .extracting(AndFilterConnective::filters, InstanceOfAssertFactories.list(SinglePointFilter.class))
          .hasSize(6)
          .satisfies(filterArguments -> {
            assertThat(filterArguments.get(0))
                .asInstanceOf(InstanceOfAssertFactories.type(GreaterThanFilter.class))
                .extracting(GreaterThanFilter::threshold, InstanceOfAssertFactories.type(TsdlSampleScalarArgument.class))
                .extracting(arg -> arg.sample().identifier().name(), InstanceOfAssertFactories.STRING)
                .isEqualTo("s2");

            assertThat(filterArguments.get(1))
                .isEqualTo(ELEMENTS.getNegatedFilter(ELEMENTS.getThresholdFilter(ThresholdFilterType.LT, ELEMENTS.getScalarArgument(3.5))));

            assertThat(filterArguments.get(2))
                .isEqualTo(
                    ELEMENTS.getNegatedFilter(ELEMENTS.getTemporalFilter(TemporalFilterType.BEFORE, Instant.parse("2022-07-03T12:45:03.123Z"))));

            assertThat(filterArguments.get(3))
                .isEqualTo(ELEMENTS.getTemporalFilter(TemporalFilterType.AFTER, Instant.parse("2022-07-03T12:45:03.123Z")));

            assertThat(filterArguments.get(4))
                .asInstanceOf(InstanceOfAssertFactories.type(NegatedSinglePointFilter.class))
                .extracting(NegatedSinglePointFilter::filter, InstanceOfAssertFactories.type(RelativeAroundFilter.class))
                .extracting(filter -> filter.referenceValue().value(), filter -> filter.maximumDeviation().value())
                .containsExactly(20.0, 34.0);

            assertThat(filterArguments.get(5))
                .asInstanceOf(InstanceOfAssertFactories.type(AbsoluteAroundFilter.class))
                .extracting(
                    absoluteAroundFilter -> ((TsdlSampleScalarArgument) absoluteAroundFilter.referenceValue()).sample().identifier().name(),
                    absoluteAroundFilter -> ((TsdlSampleScalarArgument) absoluteAroundFilter.referenceValue()).sample().aggregator().type(),
                    absoluteAroundFilter -> ((TsdlSampleScalarArgument) absoluteAroundFilter.maximumDeviation()).sample().identifier().name(),
                    absoluteAroundFilter -> ((TsdlSampleScalarArgument) absoluteAroundFilter.maximumDeviation()).sample().aggregator().type()
                )
                .containsExactly("s1", AggregatorType.AVERAGE, "s7", AggregatorType.STANDARD_DEVIATION);
          });
    }

    @ValueSource(strings = FULL_FEATURE_QUERY)
    @ParameterizedTest
    void integration_detectsSamples(String queryString) {
      var query = PARSER.parseQuery(queryString);

      assertThat(query.samples())
          .asInstanceOf(InstanceOfAssertFactories.list(TsdlSample.class))
          .hasSize(9)
          .satisfies(samples -> {
            assertAggregator(samples.get(0), AverageAggregator.class, "s1", aggregator -> assertThat(aggregator)
                .asInstanceOf(InstanceOfAssertFactories.type(AverageAggregator.class))
                .extracting(AverageAggregator::lowerBound, AverageAggregator::upperBound)
                .containsExactly(Optional.empty(), Optional.empty()));

            assertAggregator(samples.get(1), MaximumAggregator.class, "s2", aggregator -> assertThat(aggregator)
                .asInstanceOf(InstanceOfAssertFactories.type(MaximumAggregator.class))
                .extracting(MaximumAggregator::lowerBound, MaximumAggregator::upperBound)
                .containsExactly(Optional.empty(), Optional.empty()));

            assertAggregator(samples.get(2), MinimumAggregator.class, "s3", aggregator -> assertThat(aggregator)
                .asInstanceOf(InstanceOfAssertFactories.type(MinimumAggregator.class))
                .extracting(MinimumAggregator::lowerBound, MinimumAggregator::upperBound)
                .containsExactly(Optional.of(Instant.parse("2022-04-03T12:45:03.123Z")), Optional.empty()));

            assertAggregator(samples.get(3), SumAggregator.class, "s4", aggregator -> assertThat(aggregator)
                .asInstanceOf(InstanceOfAssertFactories.type(SumAggregator.class))
                .extracting(SumAggregator::lowerBound, SumAggregator::upperBound)
                .containsExactly(Optional.empty(), Optional.of(Instant.parse("2022-07-03T12:45:03.123Z"))));

            assertAggregator(samples.get(4), CountAggregator.class, "s5", aggregator -> assertThat(aggregator)
                .asInstanceOf(InstanceOfAssertFactories.type(CountAggregator.class))
                .extracting(CountAggregator::lowerBound, CountAggregator::upperBound)
                .containsExactly(Optional.of(Instant.parse("2022-04-03T12:45:03.123Z")), Optional.of(Instant.parse("2022-07-03T12:45:03.123Z"))));

            assertAggregator(samples.get(5), IntegralAggregator.class, "s6", aggregator -> assertThat(aggregator)
                .asInstanceOf(InstanceOfAssertFactories.type(IntegralAggregator.class))
                .extracting(IntegralAggregator::lowerBound, IntegralAggregator::upperBound)
                .containsExactly(Optional.of(Instant.parse("2022-04-03T12:45:03.123Z")), Optional.of(Instant.parse("2022-07-03T12:45:03.123Z"))));

            assertAggregator(samples.get(6), StandardDeviationAggregator.class, "s7", aggregator -> assertThat(aggregator)
                .asInstanceOf(InstanceOfAssertFactories.type(StandardDeviationAggregator.class))
                .extracting(StandardDeviationAggregator::lowerBound, StandardDeviationAggregator::upperBound)
                .containsExactly(Optional.empty(), Optional.empty()));

            assertAggregator(samples.get(7), TemporalAverageAggregator.class, "s8", aggregator -> assertThat(aggregator)
                .asInstanceOf(InstanceOfAssertFactories.type(TemporalAverageAggregator.class))
                .extracting(TemporalAverageAggregator::unit, TemporalAverageAggregator::periods)
                .containsExactly(
                    ParsableTsdlTimeUnit.MILLISECONDS,
                    List.of(new TimePeriodImpl(Instant.parse("2022-07-03T12:45:03.123Z"), Instant.parse("2022-07-03T12:46:03.123Z")))
                )
            );

            assertAggregator(samples.get(8), TemporalMaximumAggregator.class, "s9", aggregator -> assertThat(aggregator)
                .asInstanceOf(InstanceOfAssertFactories.type(TemporalMaximumAggregator.class))
                .extracting(TemporalMaximumAggregator::unit, TemporalMaximumAggregator::periods)
                .containsExactly(
                    ParsableTsdlTimeUnit.WEEKS,
                    List.of(
                        new TimePeriodImpl(Instant.parse("2022-07-03T12:49:03.123Z"), Instant.parse("2022-07-03T12:50:03.123Z")),
                        new TimePeriodImpl(Instant.parse("2022-07-03T12:51:03.123Z"), Instant.parse("2022-07-03T12:52:03.123Z")),
                        new TimePeriodImpl(Instant.parse("2022-07-03T12:53:03.123Z"), Instant.parse("2022-07-03T12:54:03.123Z"))
                    )
                )
            );
          });
    }

    private void assertAggregator(TsdlSample sample, Class<? extends TsdlAggregator> clazz, String identifier, Consumer<TsdlAggregator> moreChecks) {
      assertThat(sample)
          .asInstanceOf(InstanceOfAssertFactories.type(TsdlSample.class))
          .satisfies(s -> {
            assertThat(s.aggregator()).isInstanceOf(clazz);
            assertThat(s.identifier().name()).isEqualTo(identifier);
            moreChecks.accept(s.aggregator());
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
                .extracting(TsdlEvent::identifier, TsdlEvent::connective, TsdlEvent::duration)
                .containsExactly(
                    ELEMENTS.getIdentifier("low"),
                    ELEMENTS.getEventConnective(ConnectiveIdentifier.AND,
                        List.of(ELEMENTS.getThresholdFilter(ThresholdFilterType.LT, ELEMENTS.getScalarArgument(3.5)))
                    ),
                    Optional.of(
                        ELEMENTS.getDuration(
                            TsdlDurationBound.of(3, false),
                            TsdlDurationBound.of(Long.MAX_VALUE, true),
                            ParsableTsdlTimeUnit.WEEKS
                        )
                    )
                );

            assertThat(events.get(1))
                .asInstanceOf(InstanceOfAssertFactories.type(TsdlEvent.class))
                .extracting(TsdlEvent::identifier, TsdlEvent::connective, TsdlEvent::duration)
                .containsExactly(
                    ELEMENTS.getIdentifier("high"),
                    ELEMENTS.getEventConnective(ConnectiveIdentifier.OR,
                        List.of(ELEMENTS.getNegatedFilter(ELEMENTS.getThresholdFilter(ThresholdFilterType.GT, ELEMENTS.getScalarArgument(7.0))))
                    ),
                    Optional.empty()
                );

            assertThat(events.get(2))
                .asInstanceOf(InstanceOfAssertFactories.type(TsdlEvent.class))
                .satisfies(event -> {
                  assertThat(event)
                      .extracting(ev -> ev.connective().events(), InstanceOfAssertFactories.list(SinglePointFilter.class))
                      .hasSize(1)
                      .element(0, InstanceOfAssertFactories.type(GreaterThanFilter.class))
                      .extracting(GreaterThanFilter::threshold, InstanceOfAssertFactories.type(TsdlSampleScalarArgument.class))
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
          .asInstanceOf(InstanceOfAssertFactories.optional(PrecedesOperator.class))
          .get()
          .extracting(PrecedesOperator::cardinality, PrecedesOperator::operand1, PrecedesOperator::operand2, PrecedesOperator::tolerance)
          .containsExactly(2, low, high, Optional.of(
              new TsdlDurationImpl(
                  TsdlDurationBound.of(23, true),
                  TsdlDurationBound.of(26, true),
                  ParsableTsdlTimeUnit.MINUTES)));
    }

    @Test
    void integration_duplicateIdentifierDeclarationInSameGroup_throws() {
      var queryString = "WITH SAMPLES: avg() AS s1, max() AS s1\n"
          + "          YIELD: all periods";

      assertThatThrownBy(() -> PARSER.parseQuery(queryString))
          .isInstanceOf(TsdlParseException.class)
          .hasCauseInstanceOf(DuplicateIdentifierException.class);
    }

    @Test
    void integration_duplicateIdentifierDeclarationInSeparateGroup_throws() {
      var queryString = """
          WITH SAMPLES: avg() AS s1
                    USING EVENTS: AND(lt(3.5)) AS s1
                    YIELD: all periods""";

      assertThatThrownBy(() -> PARSER.parseQuery(queryString))
          .isInstanceOf(TsdlParseException.class)
          .hasCauseInstanceOf(DuplicateIdentifierException.class);
    }

    @Test
    void integration_optionalDirectives_parsedAsEmpty() {
      var queryString = "YIELD: data points";
      var query = PARSER.parseQuery(queryString);

      assertThat(query.filter()).isNotPresent();
      assertThat(query.samples()).isEmpty();
      assertThat(query.events()).isEmpty();
      assertThat(query.choice()).isNotPresent();
      assertThat(query.identifiers()).isEmpty();
      assertThat(query.result()).isNotNull();
    }
  }
}
