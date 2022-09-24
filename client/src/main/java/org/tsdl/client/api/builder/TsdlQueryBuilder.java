package org.tsdl.client.api.builder;

import java.util.Arrays;
import java.util.List;
import org.tsdl.client.impl.builder.TsdlQueryBuilderImpl;

/**
 * Facilities constructing a syntactically valid TSDL query.
 */
public interface TsdlQueryBuilder {
  TsdlQueryBuilder valueSample(ValueSampleSpecification sampleSpec);

  default TsdlQueryBuilder valueSamples(List<ValueSampleSpecification> sampleSpecs) {
    sampleSpecs.forEach(this::valueSample);
    return this;
  }

  default TsdlQueryBuilder valueSamples(ValueSampleSpecification... sampleSpecs) {
    return valueSamples(Arrays.stream(sampleSpecs).toList());
  }

  TsdlQueryBuilder temporalSample(TemporalSampleSpecification sampleSpec);

  default TsdlQueryBuilder temporalSamples(List<TemporalSampleSpecification> sampleSpecs) {
    sampleSpecs.forEach(this::temporalSample);
    return this;
  }

  default TsdlQueryBuilder temporalSamples(TemporalSampleSpecification... sampleSpecs) {
    return temporalSamples(Arrays.stream(sampleSpecs).toList());
  }

  TsdlQueryBuilder filter(FilterConnectiveSpecification filterConnectiveSpec);

  TsdlQueryBuilder event(EventSpecification eventSpec);

  default TsdlQueryBuilder events(List<EventSpecification> eventSpecs) {
    eventSpecs.forEach(this::event);
    return this;
  }

  default TsdlQueryBuilder events(EventSpecification... eventSpecs) {
    return events(Arrays.stream(eventSpecs).toList());
  }

  TsdlQueryBuilder selection(SelectSpecification choiceSpec);

  TsdlQueryBuilder yield(YieldSpecification yieldSpec);

  String build();

  static TsdlQueryBuilder instance() {
    return new TsdlQueryBuilderImpl();
  }

  static String as(String str) {
    return str;
  }
}
