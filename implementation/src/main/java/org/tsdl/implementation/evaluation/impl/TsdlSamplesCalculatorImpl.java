package org.tsdl.implementation.evaluation.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.tsdl.implementation.evaluation.TsdlEvaluationException;
import org.tsdl.implementation.evaluation.TsdlSamplesCalculator;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.connective.SinglePointFilterConnective;
import org.tsdl.implementation.model.event.TsdlEvent;
import org.tsdl.implementation.model.event.definition.ConstantEvent;
import org.tsdl.implementation.model.event.definition.EventFunction;
import org.tsdl.implementation.model.event.definition.MonotonicEvent;
import org.tsdl.implementation.model.event.definition.NegatedEventFunction;
import org.tsdl.implementation.model.filter.NegatedSinglePointFilter;
import org.tsdl.implementation.model.filter.argument.TsdlSampleScalarArgument;
import org.tsdl.implementation.model.filter.argument.TsdlScalarArgument;
import org.tsdl.implementation.model.filter.deviation.AroundFilter;
import org.tsdl.implementation.model.filter.threshold.ThresholdFilter;
import org.tsdl.implementation.model.sample.TsdlSample;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.infrastructure.model.TsdlLogEvent;

/**
 * Default implementation of {@link TsdlSamplesCalculator}.
 */
public class TsdlSamplesCalculatorImpl implements TsdlSamplesCalculator {
  @Override
  public Map<TsdlIdentifier, Double> computeSampleValues(List<TsdlSample> samples, List<DataPoint> dataPoints, List<TsdlLogEvent> logEvents) {
    return samples.stream().collect(Collectors.toMap(
            TsdlSample::identifier,
            sample -> sample.compute(dataPoints, logEvents)
        )
    );
  }

  @Override
  public void setConnectiveArgumentValues(SinglePointFilterConnective filter, List<TsdlEvent> events, Map<TsdlIdentifier, Double> sampleValues) {
    if (filter != null) {
      setSampleFilterArgumentValues(
          createSampleFilterArgumentStream(filter.filters()),
          sampleValues
      );
      validateSinglePointFilters(filter.filters());
    }

    var singlePointEventFilters = events.stream()
        .flatMap(event -> event.connective().events().stream())
        .filter(Objects::nonNull)
        .map(EventFunction.class::cast)
        .toList();

    setSampleFilterArgumentValues(
        createSampleFilterArgumentStream(singlePointEventFilters),
        sampleValues
    );
    validateSinglePointFilters(singlePointEventFilters);
  }

  private void validateSinglePointFilters(List<? extends EventFunction> filters) {
    for (var filter : filters) {
      if (!(filter instanceof AroundFilter aroundFilter)) {
        continue;
      }

      if (aroundFilter.maximumDeviation().value() < 0) {
        throw new TsdlEvaluationException("For 'around' filters, the maximum deviation must not be less than 0 because it is an absolute value.");
      }
    }
  }

  private Stream<TsdlSampleScalarArgument> createSampleFilterArgumentStream(List<? extends EventFunction> filters) {
    return filters.stream()
        .flatMap(filter -> extractFilterArguments(filter).stream())
        .filter(TsdlSampleScalarArgument.class::isInstance)
        .map(TsdlSampleScalarArgument.class::cast);
  }

  private List<TsdlScalarArgument> extractFilterArguments(EventFunction filter) {
    return switch (filter) {
      case AroundFilter aroundFilter -> List.of(aroundFilter.referenceValue(), aroundFilter.maximumDeviation());
      case ThresholdFilter thresholdFilter -> List.of(thresholdFilter.threshold());
      case ConstantEvent constantEvent -> List.of(constantEvent.maximumSlope(), constantEvent.maximumRelativeDeviation());
      case MonotonicEvent monotonicEvent -> List.of(monotonicEvent.minimumChange(), monotonicEvent.maximumChange(), monotonicEvent.tolerance());
      case NegatedEventFunction negatedEventFunction -> extractFilterArguments(negatedEventFunction.eventFunction());
      case NegatedSinglePointFilter negatedSinglePointFilter -> extractFilterArguments(negatedSinglePointFilter.filter());
      default -> List.of();
    };
  }

  private void setSampleFilterArgumentValues(Stream<TsdlSampleScalarArgument> arguments, Map<TsdlIdentifier, Double> sampleValues) {
    arguments.forEach(argument -> {
      var argumentIdentifier = argument.sample().identifier();
      if (!sampleValues.containsKey(argumentIdentifier)) {
        throw new TsdlEvaluationException(
            "Sample '%s' referenced by filter has not been computed. Is it declared in the 'SAMPLES' directive?".formatted(argumentIdentifier.name())
        );
      }

      argument.setValue(sampleValues.get(argumentIdentifier));
    });
  }
}
