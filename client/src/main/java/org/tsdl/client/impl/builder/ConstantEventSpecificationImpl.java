package org.tsdl.client.impl.builder;

import org.tsdl.client.api.builder.ComplexEventFunctionSpecification.ConstantEventSpecification;
import org.tsdl.client.api.builder.EventFunctionSpecification;

/**
 * Default implementation of {@link ConstantEventSpecification}.
 */
public class ConstantEventSpecificationImpl implements ConstantEventSpecification {
  private final String maximumSlope;
  private final String maximumRelativeDeviation;
  private final boolean negated;

  private ConstantEventSpecificationImpl(String maximumSlope, String maximumRelativeDeviation, boolean negated) {
    this.maximumSlope = maximumSlope;
    this.maximumRelativeDeviation = maximumRelativeDeviation;
    this.negated = negated;
  }

  @Override
  public String maximumSlope() {
    return maximumSlope;
  }

  @Override
  public String maximumRelativeDeviation() {
    return maximumRelativeDeviation;
  }

  @Override
  public boolean isNegated() {
    return negated;
  }

  @Override
  public EventFunctionSpecification negate() {
    return new ConstantEventSpecificationImpl(maximumSlope, maximumRelativeDeviation, !negated);
  }

  public static EventFunctionSpecification constant(String maximumSlope, String maximumRelativeDeviation, boolean negated) {
    return new ConstantEventSpecificationImpl(maximumSlope, maximumRelativeDeviation, negated);
  }

  public static EventFunctionSpecification constant(double maximumSlope, String maximumRelativeDeviation, boolean negated) {
    return constant(String.valueOf(maximumSlope), maximumRelativeDeviation, negated);
  }

  public static EventFunctionSpecification constant(String maximumSlope, double maximumRelativeDeviation, boolean negated) {
    return constant(maximumSlope, String.valueOf(maximumRelativeDeviation), negated);
  }

  public static EventFunctionSpecification constant(double maximumSlope, double maximumRelativeDeviation, boolean negated) {
    return constant(String.valueOf(maximumSlope), String.valueOf(maximumRelativeDeviation), negated);
  }

  public static EventFunctionSpecification constant(String maximumSlope, String maximumRelativeDeviation) {
    return constant(maximumSlope, maximumRelativeDeviation, false);
  }

  public static EventFunctionSpecification constant(double maximumSlope, String maximumRelativeDeviation) {
    return constant(String.valueOf(maximumSlope), maximumRelativeDeviation, false);
  }

  public static EventFunctionSpecification constant(String maximumSlope, double maximumRelativeDeviation) {
    return constant(maximumSlope, String.valueOf(maximumRelativeDeviation), false);
  }

  public static EventFunctionSpecification constant(double maximumSlope, double maximumRelativeDeviation) {
    return constant(String.valueOf(maximumSlope), String.valueOf(maximumRelativeDeviation), false);
  }
}
