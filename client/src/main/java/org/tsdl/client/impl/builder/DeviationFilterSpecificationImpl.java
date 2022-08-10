package org.tsdl.client.impl.builder;

import org.tsdl.client.api.builder.FilterSpecification;
import org.tsdl.client.api.builder.FilterSpecification.DeviationFilterSpecification;

/**
 * Default implementation of {@link DeviationFilterSpecification}.
 */
public final class DeviationFilterSpecificationImpl implements DeviationFilterSpecification {
  private final boolean negated;
  private final String reference;
  private final String maximumDeviation;
  private final DeviationFilterType type;

  private DeviationFilterSpecificationImpl(boolean negated, String reference, String maximumDeviation, DeviationFilterType type) {
    this.negated = negated;
    this.reference = reference;
    this.maximumDeviation = maximumDeviation;
    this.type = type;
  }

  @Override
  public FilterSpecification negate() {
    return new DeviationFilterSpecificationImpl(!negated, reference, maximumDeviation, type);
  }

  @Override
  public boolean isNegated() {
    return negated;
  }

  @Override
  public String reference() {
    return reference;
  }

  @Override
  public String maximumDeviation() {
    return maximumDeviation;
  }

  @Override
  public DeviationFilterType type() {
    return type;
  }

  public static DeviationFilterSpecification aroundRelative(String reference, String maximumDeviation, boolean negated) {
    return new DeviationFilterSpecificationImpl(negated, reference, maximumDeviation, DeviationFilterType.RELATIVE);
  }

  public static DeviationFilterSpecification aroundRelative(String reference, String maximumDeviation) {
    return aroundRelative(reference, maximumDeviation, false);
  }

  public static DeviationFilterSpecification aroundRelative(Double reference, String maximumDeviation, boolean negated) {
    return aroundRelative(String.valueOf(reference), maximumDeviation, negated);
  }

  public static DeviationFilterSpecification aroundRelative(Double reference, String maximumDeviation) {
    return aroundRelative(String.valueOf(reference), maximumDeviation, false);
  }

  public static DeviationFilterSpecification aroundRelative(String reference, Double maximumDeviation, boolean negated) {
    return aroundRelative(reference, String.valueOf(maximumDeviation), negated);
  }

  public static DeviationFilterSpecification aroundRelative(String reference, Double maximumDeviation) {
    return aroundRelative(reference, String.valueOf(maximumDeviation), false);
  }

  public static DeviationFilterSpecification aroundRelative(Double reference, Double maximumDeviation, boolean negated) {
    return aroundRelative(String.valueOf(reference), String.valueOf(maximumDeviation), negated);
  }

  public static DeviationFilterSpecification aroundRelative(Double reference, Double maximumDeviation) {
    return aroundRelative(String.valueOf(reference), String.valueOf(maximumDeviation), false);
  }

  public static DeviationFilterSpecification aroundAbsolute(String reference, String maximumDeviation, boolean negated) {
    return new DeviationFilterSpecificationImpl(negated, reference, maximumDeviation, DeviationFilterType.ABSOLUTE);
  }

  public static DeviationFilterSpecification aroundAbsolute(String reference, String maximumDeviation) {
    return aroundAbsolute(reference, maximumDeviation, false);
  }

  public static DeviationFilterSpecification aroundAbsolute(Double reference, String maximumDeviation, boolean negated) {
    return aroundAbsolute(String.valueOf(reference), maximumDeviation, negated);
  }

  public static DeviationFilterSpecification aroundAbsolute(Double reference, String maximumDeviation) {
    return aroundAbsolute(String.valueOf(reference), maximumDeviation, false);
  }

  public static DeviationFilterSpecification aroundAbsolute(String reference, Double maximumDeviation, boolean negated) {
    return aroundAbsolute(reference, String.valueOf(maximumDeviation), negated);
  }

  public static DeviationFilterSpecification aroundAbsolute(String reference, Double maximumDeviation) {
    return aroundAbsolute(reference, String.valueOf(maximumDeviation), false);
  }

  public static DeviationFilterSpecification aroundAbsolute(Double reference, Double maximumDeviation, boolean negated) {
    return aroundAbsolute(String.valueOf(reference), String.valueOf(maximumDeviation), negated);
  }

  public static DeviationFilterSpecification aroundAbsolute(Double reference, Double maximumDeviation) {
    return aroundAbsolute(String.valueOf(reference), String.valueOf(maximumDeviation), false);
  }
}
