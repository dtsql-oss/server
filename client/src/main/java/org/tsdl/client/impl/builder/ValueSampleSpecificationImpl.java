package org.tsdl.client.impl.builder;

import java.time.Instant;
import java.util.Optional;
import org.tsdl.client.api.builder.ValueSampleSpecification;
import org.tsdl.client.util.TsdlQueryBuildException;

/**
 * Default implementation of {@link ValueSampleSpecification}.
 */
public final class ValueSampleSpecificationImpl implements ValueSampleSpecification {
  private final String identifier;
  private final Instant lowerBound;
  private final Instant upperBound;
  private final ValueSampleType type;

  private ValueSampleSpecificationImpl(String identifier, Instant lowerBound, Instant upperBound, ValueSampleType type) {
    if (identifier == null || identifier.trim().isEmpty()) {
      throw new TsdlQueryBuildException("Sample identifier must neither be null nor blank.");
    }
    this.identifier = identifier;
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
    this.type = type;
  }

  @Override
  public String identifier() {
    return identifier;
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
  public ValueSampleType type() {
    return type;
  }

  public static ValueSampleSpecification average(String identifier, Instant lowerBound, Instant upperBound) {
    return new ValueSampleSpecificationImpl(identifier, lowerBound, upperBound, ValueSampleType.AVERAGE);
  }

  public static ValueSampleSpecification maximum(String identifier, Instant lowerBound, Instant upperBound) {
    return new ValueSampleSpecificationImpl(identifier, lowerBound, upperBound, ValueSampleType.MAXIMUM);
  }

  public static ValueSampleSpecification minimum(String identifier, Instant lowerBound, Instant upperBound) {
    return new ValueSampleSpecificationImpl(identifier, lowerBound, upperBound, ValueSampleType.MINIMUM);
  }

  public static ValueSampleSpecification sum(String identifier, Instant lowerBound, Instant upperBound) {
    return new ValueSampleSpecificationImpl(identifier, lowerBound, upperBound, ValueSampleType.SUM);
  }

  public static ValueSampleSpecification count(String identifier, Instant lowerBound, Instant upperBound) {
    return new ValueSampleSpecificationImpl(identifier, lowerBound, upperBound, ValueSampleType.COUNT);
  }

  public static ValueSampleSpecification integral(String identifier, Instant lowerBound, Instant upperBound) {
    return new ValueSampleSpecificationImpl(identifier, lowerBound, upperBound, ValueSampleType.INTEGRAL);
  }

  public static ValueSampleSpecification standardDeviation(String identifier, Instant lowerBound, Instant upperBound) {
    return new ValueSampleSpecificationImpl(identifier, lowerBound, upperBound, ValueSampleType.STANDARD_DEVIATION);
  }
}
