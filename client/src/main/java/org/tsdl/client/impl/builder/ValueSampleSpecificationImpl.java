package org.tsdl.client.impl.builder;

import java.time.Instant;
import java.util.Optional;
import org.tsdl.client.api.builder.EchoSpecification;
import org.tsdl.client.api.builder.ValueSampleSpecification;
import org.tsdl.client.util.TsdlQueryBuildException;

/**
 * Default implementation of {@link ValueSampleSpecification}.
 */
public final class ValueSampleSpecificationImpl implements ValueSampleSpecification {
  private final String identifier;
  private final Instant lowerBound;
  private final Instant upperBound;
  private final EchoSpecification echo;
  private final ValueSampleType type;

  private ValueSampleSpecificationImpl(String identifier, Instant lowerBound, Instant upperBound, EchoSpecification echo, ValueSampleType type) {
    if (identifier == null || identifier.trim().isEmpty()) {
      throw new TsdlQueryBuildException("Sample identifier must neither be null nor blank.");
    }
    this.identifier = identifier;
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
    this.echo = echo;
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
  public Optional<EchoSpecification> echo() {
    return Optional.ofNullable(echo);
  }

  @Override
  public ValueSampleType type() {
    return type;
  }

  public static ValueSampleSpecification average(String identifier, Instant lowerBound, Instant upperBound, EchoSpecification echo) {
    return new ValueSampleSpecificationImpl(identifier, lowerBound, upperBound, echo, ValueSampleType.AVERAGE);
  }

  public static ValueSampleSpecification average(String identifier, Instant lowerBound, Instant upperBound) {
    return average(identifier, lowerBound, upperBound, null);
  }

  public static ValueSampleSpecification average(String identifier, String lowerBound, String upperBound) {
    return average(identifier, instantOrNull(lowerBound), instantOrNull(upperBound));
  }

  public static ValueSampleSpecification average(String identifier, String lowerBound, String upperBound, EchoSpecification echo) {
    return average(identifier, instantOrNull(lowerBound), instantOrNull(upperBound), echo);
  }

  public static ValueSampleSpecification average(String identifier) {
    return average(identifier, (String) null, null);
  }

  public static ValueSampleSpecification average(String identifier, EchoSpecification echo) {
    return average(identifier, (String) null, null, echo);
  }


  public static ValueSampleSpecification maximum(String identifier, Instant lowerBound, Instant upperBound, EchoSpecification echo) {
    return new ValueSampleSpecificationImpl(identifier, lowerBound, upperBound, echo, ValueSampleType.MAXIMUM);
  }

  public static ValueSampleSpecification maximum(String identifier, Instant lowerBound, Instant upperBound) {
    return maximum(identifier, lowerBound, upperBound, null);
  }

  public static ValueSampleSpecification maximum(String identifier, String lowerBound, String upperBound, EchoSpecification echo) {
    return maximum(identifier, instantOrNull(lowerBound), instantOrNull(upperBound), echo);
  }

  public static ValueSampleSpecification maximum(String identifier, String lowerBound, String upperBound) {
    return maximum(identifier, instantOrNull(lowerBound), instantOrNull(upperBound));
  }

  public static ValueSampleSpecification maximum(String identifier, EchoSpecification echo) {
    return maximum(identifier, (String) null, null, echo);
  }

  public static ValueSampleSpecification maximum(String identifier) {
    return maximum(identifier, (String) null, null);
  }


  public static ValueSampleSpecification minimum(String identifier, Instant lowerBound, Instant upperBound, EchoSpecification echo) {
    return new ValueSampleSpecificationImpl(identifier, lowerBound, upperBound, echo, ValueSampleType.MINIMUM);
  }

  public static ValueSampleSpecification minimum(String identifier, Instant lowerBound, Instant upperBound) {
    return minimum(identifier, lowerBound, upperBound, null);
  }

  public static ValueSampleSpecification minimum(String identifier, String lowerBound, String upperBound, EchoSpecification echo) {
    return minimum(identifier, instantOrNull(lowerBound), instantOrNull(upperBound), echo);
  }

  public static ValueSampleSpecification minimum(String identifier, String lowerBound, String upperBound) {
    return minimum(identifier, instantOrNull(lowerBound), instantOrNull(upperBound));
  }

  public static ValueSampleSpecification minimum(String identifier, EchoSpecification echo) {
    return minimum(identifier, (String) null, null, echo);
  }

  public static ValueSampleSpecification minimum(String identifier) {
    return minimum(identifier, (String) null, null);
  }


  public static ValueSampleSpecification sum(String identifier, Instant lowerBound, Instant upperBound, EchoSpecification echo) {
    return new ValueSampleSpecificationImpl(identifier, lowerBound, upperBound, echo, ValueSampleType.SUM);
  }

  public static ValueSampleSpecification sum(String identifier, Instant lowerBound, Instant upperBound) {
    return sum(identifier, lowerBound, upperBound, null);
  }

  public static ValueSampleSpecification sum(String identifier, String lowerBound, String upperBound, EchoSpecification echo) {
    return sum(identifier, instantOrNull(lowerBound), instantOrNull(upperBound), echo);
  }

  public static ValueSampleSpecification sum(String identifier, String lowerBound, String upperBound) {
    return sum(identifier, instantOrNull(lowerBound), instantOrNull(upperBound));
  }

  public static ValueSampleSpecification sum(String identifier, EchoSpecification echo) {
    return sum(identifier, (String) null, null, echo);
  }

  public static ValueSampleSpecification sum(String identifier) {
    return sum(identifier, (String) null, null);
  }


  public static ValueSampleSpecification count(String identifier, Instant lowerBound, Instant upperBound, EchoSpecification echo) {
    return new ValueSampleSpecificationImpl(identifier, lowerBound, upperBound, echo, ValueSampleType.COUNT);
  }

  public static ValueSampleSpecification count(String identifier, Instant lowerBound, Instant upperBound) {
    return count(identifier, lowerBound, upperBound, null);
  }

  public static ValueSampleSpecification count(String identifier, String lowerBound, String upperBound, EchoSpecification echo) {
    return count(identifier, instantOrNull(lowerBound), instantOrNull(upperBound), echo);
  }

  public static ValueSampleSpecification count(String identifier, String lowerBound, String upperBound) {
    return count(identifier, instantOrNull(lowerBound), instantOrNull(upperBound));
  }

  public static ValueSampleSpecification count(String identifier, EchoSpecification echo) {
    return count(identifier, (String) null, null, echo);
  }

  public static ValueSampleSpecification count(String identifier) {
    return count(identifier, (String) null, null);
  }


  public static ValueSampleSpecification integral(String identifier, Instant lowerBound, Instant upperBound, EchoSpecification echo) {
    return new ValueSampleSpecificationImpl(identifier, lowerBound, upperBound, echo, ValueSampleType.INTEGRAL);
  }

  public static ValueSampleSpecification integral(String identifier, Instant lowerBound, Instant upperBound) {
    return integral(identifier, lowerBound, upperBound, null);
  }

  public static ValueSampleSpecification integral(String identifier, String lowerBound, String upperBound, EchoSpecification echo) {
    return integral(identifier, instantOrNull(lowerBound), instantOrNull(upperBound), echo);
  }

  public static ValueSampleSpecification integral(String identifier, String lowerBound, String upperBound) {
    return integral(identifier, instantOrNull(lowerBound), instantOrNull(upperBound));
  }

  public static ValueSampleSpecification integral(String identifier, EchoSpecification echo) {
    return integral(identifier, (String) null, null, echo);
  }

  public static ValueSampleSpecification integral(String identifier) {
    return integral(identifier, (String) null, null);
  }


  public static ValueSampleSpecification standardDeviation(String identifier, Instant lowerBound, Instant upperBound, EchoSpecification echo) {
    return new ValueSampleSpecificationImpl(identifier, lowerBound, upperBound, echo, ValueSampleType.STANDARD_DEVIATION);
  }

  public static ValueSampleSpecification standardDeviation(String identifier, Instant lowerBound, Instant upperBound) {
    return standardDeviation(identifier, lowerBound, upperBound, null);
  }

  public static ValueSampleSpecification standardDeviation(String identifier, String lowerBound, String upperBound, EchoSpecification echo) {
    return standardDeviation(identifier, instantOrNull(lowerBound), instantOrNull(upperBound), echo);
  }
  
  public static ValueSampleSpecification standardDeviation(String identifier, String lowerBound, String upperBound) {
    return standardDeviation(identifier, instantOrNull(lowerBound), instantOrNull(upperBound));
  }

  public static ValueSampleSpecification standardDeviation(String identifier, EchoSpecification echo) {
    return standardDeviation(identifier, (String) null, null, echo);
  }

  public static ValueSampleSpecification standardDeviation(String identifier) {
    return standardDeviation(identifier, (String) null, null);
  }

  private static Instant instantOrNull(String str) {
    return str == null ? null : BuilderUtil.requireInstant(str);
  }
}
