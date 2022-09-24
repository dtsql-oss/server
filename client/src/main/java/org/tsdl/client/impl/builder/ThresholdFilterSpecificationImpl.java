package org.tsdl.client.impl.builder;

import org.tsdl.client.api.builder.FilterSpecification;
import org.tsdl.client.api.builder.FilterSpecification.ThresholdFilterSpecification;

/**
 * Default implementation of {@link ThresholdFilterSpecification}.
 */
public final class ThresholdFilterSpecificationImpl implements ThresholdFilterSpecification {
  private final String threshold;
  private final boolean negated;
  private final ThresholdFilterType type;

  private ThresholdFilterSpecificationImpl(String threshold, boolean negated, ThresholdFilterType type) {
    this.threshold = threshold;
    this.negated = negated;
    this.type = type;
  }

  @Override
  public boolean isNegated() {
    return negated;
  }

  @Override
  public String threshold() {
    return threshold;
  }

  @Override
  public ThresholdFilterType type() {
    return type;
  }

  @Override
  public FilterSpecification negate() {
    return new ThresholdFilterSpecificationImpl(threshold, !negated, type);
  }

  public static ThresholdFilterSpecification lt(String threshold) {
    return lt(threshold, false);
  }

  public static ThresholdFilterSpecification lt(double threshold) {
    return lt(String.valueOf(threshold), false);
  }

  public static ThresholdFilterSpecification lt(double threshold, boolean negated) {
    return lt(String.valueOf(threshold), negated);
  }

  public static ThresholdFilterSpecification lt(String threshold, boolean negated) {
    return new ThresholdFilterSpecificationImpl(threshold, negated, ThresholdFilterType.LESS_THAN);
  }

  public static ThresholdFilterSpecification gt(String threshold) {
    return gt(threshold, false);
  }

  public static ThresholdFilterSpecification gt(double threshold) {
    return gt(String.valueOf(threshold), false);
  }

  public static ThresholdFilterSpecification gt(double threshold, boolean negated) {
    return gt(String.valueOf(threshold), negated);
  }

  public static ThresholdFilterSpecification gt(String threshold, boolean negated) {
    return new ThresholdFilterSpecificationImpl(threshold, negated, ThresholdFilterType.GREATER_THAN);
  }
}
