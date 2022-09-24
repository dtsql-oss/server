package org.tsdl.client.impl.builder;

import java.util.Optional;
import org.tsdl.client.api.builder.Range;
import org.tsdl.infrastructure.common.TsdlTimeUnit;

/**
 * Default implementation of {@link Range}.
 */
public final class RangeImpl implements Range {
  private final Long lowerBound;
  private final Long upperBound;
  private final TsdlTimeUnit unit;
  private final IntervalType type;

  private RangeImpl(Long lowerBound, Long upperBound, TsdlTimeUnit unit, IntervalType type) {
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
    this.unit = unit;
    this.type = type;
  }

  @Override
  public Optional<Long> lowerBound() {
    return Optional.ofNullable(lowerBound);
  }

  @Override
  public Optional<Long> upperBound() {
    return Optional.ofNullable(upperBound);
  }

  @Override
  public TsdlTimeUnit unit() {
    return unit;
  }

  @Override
  public IntervalType type() {
    return type;
  }

  public static Range for_(Long lowerBound, Long upperBound, TsdlTimeUnit unit, IntervalType type) {
    return new RangeImpl(lowerBound, upperBound, unit, type);
  }

  public static Range for_(Long upperBound, TsdlTimeUnit unit, IntervalType type) {
    return for_(null, upperBound, unit, type);
  }

  public static Range for_(Long lowerBound, IntervalType type, TsdlTimeUnit unit) {
    return for_(lowerBound, null, unit, type);
  }

  public static Range for_(TsdlTimeUnit unit, IntervalType type) {
    return for_(null, null, unit, type);
  }

  public static Range within(Long lowerBound, Long upperBound, TsdlTimeUnit unit, IntervalType type) {
    return for_(lowerBound, upperBound, unit, type);
  }

  public static Range within(Long upperBound, TsdlTimeUnit unit, IntervalType type) {
    return for_(null, upperBound, unit, type);
  }

  public static Range within(Long lowerBound, IntervalType type, TsdlTimeUnit unit) {
    return for_(lowerBound, null, unit, type);
  }

  public static Range within(TsdlTimeUnit unit, IntervalType type) {
    return for_(null, null, unit, type);
  }
}
