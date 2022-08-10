package org.tsdl.implementation.evaluation.impl.sample.aggregation.value;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.tsdl.implementation.model.sample.aggregation.TsdlAggregator;
import org.tsdl.implementation.model.sample.aggregation.value.ValueAggregator;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Abstract base class for {@link TsdlAggregator} implementations that encapsulates common tasks such as logging and preparing a list of
 * {@link DataPoint} actually to be processed by a concrete {@link TsdlAggregator} implementation, based on its {@link ValueAggregator#lowerBound()}
 * and {@link ValueAggregator#upperBound()} values.
 */
@Slf4j
abstract class AbstractValueAggregator implements ValueAggregator {
  private final Predicate<DataPoint> lowerBoundChecker = dp -> lowerBound().isEmpty() || !dp.timestamp().isBefore(lowerBound().get());
  private final Predicate<DataPoint> upperBoundChecker = dp -> upperBound().isEmpty() || !dp.timestamp().isAfter(upperBound().get());

  private double sampleValue = Double.NaN;

  private final String descriptor;

  protected final Instant lowerBound;

  protected final Instant upperBound;

  /**
   * Initializes a {@link AbstractValueAggregator} instance.
   */
  protected AbstractValueAggregator(Instant lowerBound, Instant upperBound) {
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
    this.descriptor = "%s from %s until %s".formatted(
        type(),
        lowerBound().isPresent() ? lowerBound().get() : "<beginning>",
        upperBound().isPresent() ? upperBound().get() : "<end>"
    );
  }

  protected abstract double aggregate(List<DataPoint> input);

  @Override
  public double compute(String sampleIdentifier, List<DataPoint> dataPoints) {
    Conditions.checkNotNull(Condition.ARGUMENT, dataPoints, "Aggregator input must not be null");
    log.info("Calculating sample '{}' ({}) over {} data points.", sampleIdentifier, descriptor, dataPoints.size());

    var valueStream = getAggregatorInput(dataPoints);
    sampleValue = aggregate(valueStream);
    Conditions.checkNotNull(Condition.STATE, sampleValue, "Sample computation failed, aggregate value must not be null.");

    log.info("Calculated sample '{}' ({}) to be {}.", sampleIdentifier, descriptor, sampleValue);

    return sampleValue;
  }

  @Override
  public double value() {
    Conditions.checkIsTrue(Condition.STATE, this::isComputed, "Value of %s must have been computed before trying to access it.", descriptor);
    return sampleValue;
  }

  @Override
  public Optional<Instant> lowerBound() {
    return Optional.ofNullable(lowerBound);
  }

  @Override
  public Optional<Instant> upperBound() {
    return Optional.ofNullable(upperBound);
  }

  @Override
  public boolean isComputed() {
    return !Double.isNaN(sampleValue);
  }

  private List<DataPoint> getAggregatorInput(List<DataPoint> dataPoints) {
    return dataPoints.stream()
        .filter(dp -> lowerBoundChecker.test(dp) && upperBoundChecker.test(dp))
        .toList();
  }
}
