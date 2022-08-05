package org.tsdl.implementation.evaluation.impl;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.tsdl.implementation.evaluation.TsdlEvaluationException;
import org.tsdl.implementation.evaluation.TsdlSamplesCalculator;
import org.tsdl.implementation.factory.TsdlComponentFactory;
import org.tsdl.implementation.math.SummaryStatistics;
import org.tsdl.implementation.model.TsdlQuery;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.event.definition.SinglePointEventDefinition;
import org.tsdl.implementation.model.filter.NegatedSinglePointFilter;
import org.tsdl.implementation.model.filter.SinglePointFilter;
import org.tsdl.implementation.model.filter.threshold.ThresholdFilter;
import org.tsdl.implementation.model.filter.threshold.argument.TsdlSampleFilterArgument;
import org.tsdl.implementation.model.sample.TsdlSample;
import org.tsdl.implementation.model.sample.aggregation.SummaryAggregator;
import org.tsdl.implementation.model.sample.aggregation.TsdlAggregator;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.infrastructure.model.TsdlLogEvent;

/**
 * Default implementation of {@link TsdlSamplesCalculator}.
 */
public class TsdlSamplesCalculatorImpl implements TsdlSamplesCalculator {

  /**
   * <p>
   * Different samples may have different lower and upper bounds. Therefore, we may reuse {@link SummaryStatistics} instances only if both lower and
   * upper bound are equal. For instance, all global samples may share the same instance as well as local samples with the same bounds, but local
   * samples with differing bounds may not.
   * </p>
   * <p>
   * This record encapsulates the lower and upper bound of a {@link TsdlAggregator}, enabling support for memorizing {@link SummaryStatistics}
   * instances that may be reused.
   * </p>
   */
  private record AggregatorBounds(Instant lowerBound, Instant upperBound) {
  }

  @Override
  @SuppressWarnings("ConstantConditions") // method too complex for this inspection due to the "summaryAggregator" pattern variable, but logic is fine
  public void setSummaryStatisticsCalculator(List<TsdlSample> samples) {
    final var summaryStatisticsByBounds = new HashMap<AggregatorBounds, SummaryStatistics>();

    for (TsdlSample sample : samples) {
      if (!(sample.aggregator() instanceof SummaryAggregator summaryAggregator)) {
        continue;
      }

      var bounds = new AggregatorBounds(summaryAggregator.lowerBound().orElse(null), summaryAggregator.upperBound().orElse(null));
      var statistics = summaryStatisticsByBounds.computeIfAbsent(bounds, k -> TsdlComponentFactory.INSTANCE.summaryStatistics());
      summaryAggregator.setStatistics(statistics);
    }
  }

  @Override
  public Map<TsdlIdentifier, Double> computeSampleValues(List<TsdlSample> samples, List<DataPoint> dataPoints, List<TsdlLogEvent> logEvents) {
    return samples.stream().collect(Collectors.toMap(
            TsdlSample::identifier,
            sample -> sample.compute(dataPoints, logEvents)
        )
    );
  }

  @Override
  public void setConnectiveArgumentValues(TsdlQuery query, Map<TsdlIdentifier, Double> sampleValues) {
    if (query.filter().isPresent()) {
      setThresholdFilterSampleArguments(
          createThresholdFilterStream(query.filter().get().filters()),
          sampleValues
      );
    }

    var singlePointEvents = query.events().stream()
        .filter(event -> event.definition() instanceof SinglePointEventDefinition)
        .map(event -> (SinglePointEventDefinition) event.definition())
        .flatMap(eventDefinition -> eventDefinition.connective().filters().stream())
        .toList();

    setThresholdFilterSampleArguments(
        createThresholdFilterStream(singlePointEvents),
        sampleValues
    );
  }

  private Stream<ThresholdFilter> createThresholdFilterStream(List<SinglePointFilter> filters) {
    // constructs like lt(sampleIdentifier)
    var nonNegated = filters.stream()
        .filter(ThresholdFilter.class::isInstance)
        .map(ThresholdFilter.class::cast)
        .filter(filter -> filter.threshold() instanceof TsdlSampleFilterArgument);

    // constructs like NOT(gt(sampleIdentifier))
    var negated = filters.stream()
        .filter(NegatedSinglePointFilter.class::isInstance)
        .map(NegatedSinglePointFilter.class::cast)
        .filter(filter -> filter.filter() instanceof ThresholdFilter)
        .map(filter -> (ThresholdFilter) filter.filter())
        .filter(filter -> filter.threshold() instanceof TsdlSampleFilterArgument);

    return Stream.concat(nonNegated, negated);
  }

  @SuppressWarnings("ConstantConditions") // method too complex for this inspection due to the "filterArgument" pattern variable, but logic is fine
  private void setThresholdFilterSampleArguments(Stream<ThresholdFilter> filters, Map<TsdlIdentifier, Double> sampleValues) {
    filters.forEach(thresholdFilter -> {
      if (!(thresholdFilter.threshold() instanceof TsdlSampleFilterArgument filterArgument)) {
        throw Conditions.exception(Condition.ARGUMENT, "Filter argument must reference a sample (non-literal).");
      }

      var argumentIdentifier = filterArgument.sample().identifier();
      if (!sampleValues.containsKey(argumentIdentifier)) {
        throw new TsdlEvaluationException(
            "Sample '%s' referenced by filter has not been computed. Is it declared in the 'SAMPLES' directive?".formatted(argumentIdentifier.name())
        );
      }

      filterArgument.setValue(sampleValues.get(argumentIdentifier));
    });
  }
}
