package org.tsdl.client.impl.builder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.tsdl.client.api.builder.FilterSpecification.not;
import static org.tsdl.client.api.builder.Range.IntervalType.OPEN_START;
import static org.tsdl.client.api.builder.TsdlQueryBuilder.as;
import static org.tsdl.client.impl.builder.ChoiceSpecificationImpl.precedes;
import static org.tsdl.client.impl.builder.EventSpecificationImpl.event;
import static org.tsdl.client.impl.builder.FilterConnectiveSpecificationImpl.and;
import static org.tsdl.client.impl.builder.FilterConnectiveSpecificationImpl.or;
import static org.tsdl.client.impl.builder.QueryPeriodImpl.period;
import static org.tsdl.client.impl.builder.RangeImpl.within;
import static org.tsdl.client.impl.builder.TemporalFilterSpecificationImpl.after;
import static org.tsdl.client.impl.builder.TemporalFilterSpecificationImpl.before;
import static org.tsdl.client.impl.builder.TemporalSampleSpecificationImpl.countTemporal;
import static org.tsdl.client.impl.builder.TemporalSampleSpecificationImpl.maximumTemporal;
import static org.tsdl.client.impl.builder.ThresholdFilterSpecificationImpl.gt;
import static org.tsdl.client.impl.builder.ThresholdFilterSpecificationImpl.lt;
import static org.tsdl.client.impl.builder.ValueSampleSpecificationImpl.average;
import static org.tsdl.client.impl.builder.ValueSampleSpecificationImpl.integral;
import static org.tsdl.client.impl.builder.ValueSampleSpecificationImpl.standardDeviation;
import static org.tsdl.client.impl.builder.ValueSampleSpecificationImpl.sum;
import static org.tsdl.client.impl.builder.YieldSpecificationImpl.allPeriods;
import static org.tsdl.client.impl.builder.YieldSpecificationImpl.dataPoints;
import static org.tsdl.client.impl.builder.YieldSpecificationImpl.samples;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.tsdl.client.api.builder.ChoiceSpecification;
import org.tsdl.client.api.builder.EventSpecification;
import org.tsdl.client.api.builder.FilterConnectiveSpecification;
import org.tsdl.client.api.builder.TemporalSampleSpecification;
import org.tsdl.client.api.builder.TsdlQueryBuilder;
import org.tsdl.client.api.builder.ValueSampleSpecification;
import org.tsdl.client.api.builder.YieldSpecification;
import org.tsdl.client.util.TsdlQueryBuildException;
import org.tsdl.infrastructure.common.TsdlTimeUnit;

class TsdlQueryBuilderTest {

  @ParameterizedTest
  @MethodSource("org.tsdl.client.impl.builder.stub.TsdlQueryBuilderTestDataFactory#temporalSampleInput")
  void temporalSample(TemporalSampleSpecification specification, String expectedSample) {
    var query = TsdlQueryBuilder.instance()
        .temporalSample(specification)
        .yield(dataPoints());

    var expectedQuery = """
        WITH SAMPLES:
          %s
        YIELD:
          data points""".formatted(expectedSample);

    assertThat(query.build()).isEqualTo(expectedQuery);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.client.impl.builder.stub.TsdlQueryBuilderTestDataFactory#temporalSamplesInput")
  void temporalSamples(List<TemporalSampleSpecification> specifications, String expectedSamples) {
    var query = TsdlQueryBuilder.instance()
        .temporalSamples(specifications)
        .yield(dataPoints());

    var expectedQuery = """
        WITH SAMPLES:
          %s
        YIELD:
          data points""".formatted(expectedSamples);

    assertThat(query.build()).isEqualTo(expectedQuery);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.client.impl.builder.stub.TsdlQueryBuilderTestDataFactory#valueSampleInput")
  void valueSample(ValueSampleSpecification specification, String expectedSample) {
    var query = TsdlQueryBuilder.instance()
        .valueSample(specification)
        .yield(dataPoints());

    var expectedQuery = """
        WITH SAMPLES:
          %s
        YIELD:
          data points""".formatted(expectedSample);

    assertThat(query.build()).isEqualTo(expectedQuery);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.client.impl.builder.stub.TsdlQueryBuilderTestDataFactory#valueSamplesInput")
  void valueSamples(List<ValueSampleSpecification> specifications, String expectedSamples) {
    var query = TsdlQueryBuilder.instance()
        .valueSamples(specifications)
        .yield(dataPoints());

    var expectedQuery = """
        WITH SAMPLES:
          %s
        YIELD:
          data points""".formatted(expectedSamples);

    assertThat(query.build()).isEqualTo(expectedQuery);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.client.impl.builder.stub.TsdlQueryBuilderTestDataFactory#filterInput")
  void filter(FilterConnectiveSpecification specification, String expectedFilter) {
    var query = TsdlQueryBuilder.instance()
        .filter(specification)
        .yield(dataPoints());

    var expectedQuery = """
        APPLY FILTER:
          %s
        YIELD:
          data points""".formatted(expectedFilter);

    assertThat(query.build()).isEqualTo(expectedQuery);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.client.impl.builder.stub.TsdlQueryBuilderTestDataFactory#eventInput")
  void events(EventSpecification eventSpecification, String expectedEvent) {
    var query = TsdlQueryBuilder.instance()
        .event(eventSpecification)
        .yield(dataPoints());

    var expectedQuery = """
        USING EVENTS:
          %s
        YIELD:
          data points""".formatted(expectedEvent);

    assertThat(query.build()).isEqualTo(expectedQuery);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.client.impl.builder.stub.TsdlQueryBuilderTestDataFactory#eventsInput")
  void events(List<EventSpecification> eventSpecifications, String expectedEvents) {
    var query = TsdlQueryBuilder.instance()
        .events(eventSpecifications)
        .yield(dataPoints());

    var expectedQuery = """
        USING EVENTS:
          %s
        YIELD:
          data points""".formatted(expectedEvents);

    assertThat(query.build()).isEqualTo(expectedQuery);
  }


  @ParameterizedTest
  @MethodSource("org.tsdl.client.impl.builder.stub.TsdlQueryBuilderTestDataFactory#choiceInput")
  void choice(ChoiceSpecification choiceSpecification, String expectedChoice) {
    var query = TsdlQueryBuilder.instance()
        .choice(choiceSpecification)
        .yield(dataPoints());

    var expectedQuery = """
        CHOOSE:
          %s
        YIELD:
          data points""".formatted(expectedChoice);

    assertThat(query.build()).isEqualTo(expectedQuery);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.client.impl.builder.stub.TsdlQueryBuilderTestDataFactory#yieldInput")
  void choice(YieldSpecification yieldSpecification, String expectedChoice) {
    var query = TsdlQueryBuilder.instance()
        .yield(yieldSpecification);

    var expectedQuery = """
        YIELD:
          %s""".formatted(expectedChoice);

    assertThat(query.build()).isEqualTo(expectedQuery);
  }

  @Test
  void buildWithoutYield_throws() {
    final var builder = TsdlQueryBuilder.instance();
    assertThatThrownBy(builder::build).isInstanceOf(TsdlQueryBuildException.class);
  }

  @Test
  void test() {
    System.out.println(
        TsdlQueryBuilder.instance()
            .temporalSamples(
                maximumTemporal(
                    "s1",
                    TsdlTimeUnit.MILLISECONDS,
                    period("2022-07-08T12:30:14.123+03:00", "2022-07-09T12:30:14.123+03:00"),
                    period("2022-07-10T12:30:14.123+03:00", "2022-07-11T12:30:14.123+03:00")
                ),
                countTemporal(
                    "s2",
                    period("2022-07-08T12:30:14.123+03:00", "2022-07-09T12:30:14.123+03:00"),
                    period("2022-07-10T12:30:14.123+03:00", "2022-07-11T12:30:14.123+03:00")
                )
            )
            .valueSamples(
                average("s3", Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS)),
                integral("s4", null, Instant.now().plus(1, ChronoUnit.DAYS)),
                standardDeviation("s5", Instant.now(), null),
                sum("s6", (String) null, null)
            )
            .filter(
                and(
                    gt("s2"),
                    not(lt(3.5)),
                    not(before("2022-07-03T12:45:03.123Z")),
                    after(Instant.parse("2022-07-03T12:45:03.123Z")),
                    DeviationFilterSpecificationImpl.aroundRelative(20.0, 34.0, true),
                    DeviationFilterSpecificationImpl.aroundAbsolute("s1", "s7")
                )
            )
            .events(
                event(and(lt(3.5)), RangeImpl.for_(3L, OPEN_START, TsdlTimeUnit.WEEKS), as("low")),
                event(or(not(gt(7.0))), as("high")),
                event(and(gt("s2")), as("mid"))
            )
            .choice(precedes("low", "high", within(23L, 26L, TsdlTimeUnit.MINUTES, OPEN_START)))
            .yield(samples("s1", "s2", "s3"))
            .yield(allPeriods())
            .build()
    );
  }
}
