package org.tsdl.client.impl.builder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.tsdl.client.impl.builder.YieldSpecificationImpl.dataPoints;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.tsdl.client.api.builder.EventSpecification;
import org.tsdl.client.api.builder.FilterConnectiveSpecification;
import org.tsdl.client.api.builder.SelectSpecification;
import org.tsdl.client.api.builder.TemporalSampleSpecification;
import org.tsdl.client.api.builder.TsdlQueryBuilder;
import org.tsdl.client.api.builder.ValueSampleSpecification;
import org.tsdl.client.api.builder.YieldSpecification;
import org.tsdl.client.util.TsdlQueryBuildException;

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

    assertThat(query.build()).isEqualToNormalizingNewlines(expectedQuery);
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

    assertThat(query.build()).isEqualToNormalizingNewlines(expectedQuery);
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

    assertThat(query.build()).isEqualToNormalizingNewlines(expectedQuery);
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

    assertThat(query.build()).isEqualToNormalizingNewlines(expectedQuery);
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

    assertThat(query.build()).isEqualToNormalizingNewlines(expectedQuery);
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

    assertThat(query.build()).isEqualToNormalizingNewlines(expectedQuery);
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

    assertThat(query.build()).isEqualToNormalizingNewlines(expectedQuery);
  }


  @ParameterizedTest
  @MethodSource("org.tsdl.client.impl.builder.stub.TsdlQueryBuilderTestDataFactory#selectionInput")
  void selection(SelectSpecification choiceSpecification, String expectedSelection) {
    var query = TsdlQueryBuilder.instance()
        .selection(choiceSpecification)
        .yield(dataPoints());

    var expectedQuery = """
        SELECT:
          %s
        YIELD:
          data points""".formatted(expectedSelection);

    assertThat(query.build()).isEqualToNormalizingNewlines(expectedQuery);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.client.impl.builder.stub.TsdlQueryBuilderTestDataFactory#yieldInput")
  void yield(YieldSpecification yieldSpecification, String expectedChoice) {
    var query = TsdlQueryBuilder.instance()
        .yield(yieldSpecification);

    var expectedQuery = """
        YIELD:
          %s""".formatted(expectedChoice);

    assertThat(query.build()).isEqualToNormalizingNewlines(expectedQuery);
  }

  @Test
  void buildWithoutYield_throws() {
    final var builder = TsdlQueryBuilder.instance();
    assertThatThrownBy(builder::build).isInstanceOf(TsdlQueryBuildException.class);
  }
}
