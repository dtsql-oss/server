package org.tsdl.client.impl.builder;

import org.tsdl.client.api.builder.ComplexEventFunctionSpecification.MonotonicEventSpecification;
import org.tsdl.client.api.builder.EventFunctionSpecification;

/**
 * Default implementation of {@link MonotonicEventSpecification} for type {@link MonotonicEventType#DECREASE}.
 */
public class DecreaseEventSpecificationImpl implements MonotonicEventSpecification {
  private final String minimumChange;
  private final String maximumChange;
  private final String tolerance;
  private final boolean negated;

  private DecreaseEventSpecificationImpl(String minimumChange, String maximumChange, String tolerance, boolean negated) {
    this.minimumChange = minimumChange;
    this.maximumChange = maximumChange;
    this.tolerance = tolerance;
    this.negated = negated;
  }

  @Override
  public String minimumChange() {
    return minimumChange;
  }

  @Override
  public String maximumChange() {
    return maximumChange;
  }

  @Override
  public String tolerance() {
    return tolerance;
  }

  @Override
  public MonotonicEventType type() {
    return MonotonicEventType.DECREASE;
  }

  @Override
  public boolean isNegated() {
    return negated;
  }

  @Override
  public EventFunctionSpecification negate() {
    return new DecreaseEventSpecificationImpl(minimumChange, maximumChange, tolerance, !negated);
  }

  public static EventFunctionSpecification decrease(String minimumChange, String maximumChange, String tolerance, boolean negated) {
    return new DecreaseEventSpecificationImpl(minimumChange, maximumChange, tolerance, negated);
  }

  public static EventFunctionSpecification decrease(String minimumChange, String maximumChange, double tolerance, boolean negated) {
    return decrease(minimumChange, maximumChange, String.valueOf(tolerance), negated);
  }

  public static EventFunctionSpecification decrease(String minimumChange, double maximumChange, String tolerance, boolean negated) {
    return decrease(minimumChange, BuilderUtil.monotonicUpperBound(maximumChange), tolerance, negated);
  }

  public static EventFunctionSpecification decrease(String minimumChange, double maximumChange, double tolerance, boolean negated) {
    return decrease(minimumChange, BuilderUtil.monotonicUpperBound(maximumChange), String.valueOf(tolerance), negated);
  }

  public static EventFunctionSpecification decrease(double minimumChange, String maximumChange, String tolerance, boolean negated) {
    return decrease(String.valueOf(minimumChange), maximumChange, tolerance, negated);
  }

  public static EventFunctionSpecification decrease(double minimumChange, String maximumChange, double tolerance, boolean negated) {
    return decrease(String.valueOf(minimumChange), maximumChange, String.valueOf(tolerance), negated);
  }

  public static EventFunctionSpecification decrease(double minimumChange, double maximumChange, String tolerance, boolean negated) {
    return decrease(String.valueOf(minimumChange), BuilderUtil.monotonicUpperBound(maximumChange), tolerance, negated);
  }

  public static EventFunctionSpecification decrease(double minimumChange, double maximumChange, double tolerance, boolean negated) {
    return decrease(String.valueOf(minimumChange), BuilderUtil.monotonicUpperBound(maximumChange), String.valueOf(tolerance), negated);
  }

  public static EventFunctionSpecification decrease(String minimumChange, String maximumChange, String tolerance) {
    return decrease(minimumChange, maximumChange, tolerance, false);
  }

  public static EventFunctionSpecification decrease(String minimumChange, String maximumChange, double tolerance) {
    return decrease(minimumChange, maximumChange, String.valueOf(tolerance), false);
  }

  public static EventFunctionSpecification decrease(String minimumChange, double maximumChange, String tolerance) {
    return decrease(minimumChange, BuilderUtil.monotonicUpperBound(maximumChange), tolerance, false);
  }

  public static EventFunctionSpecification decrease(String minimumChange, double maximumChange, double tolerance) {
    return decrease(minimumChange, BuilderUtil.monotonicUpperBound(maximumChange), String.valueOf(tolerance), false);
  }

  public static EventFunctionSpecification decrease(double minimumChange, String maximumChange, String tolerance) {
    return decrease(String.valueOf(minimumChange), maximumChange, tolerance, false);
  }

  public static EventFunctionSpecification decrease(double minimumChange, String maximumChange, double tolerance) {
    return decrease(String.valueOf(minimumChange), maximumChange, String.valueOf(tolerance), false);
  }

  public static EventFunctionSpecification decrease(double minimumChange, double maximumChange, String tolerance) {
    return decrease(String.valueOf(minimumChange), BuilderUtil.monotonicUpperBound(maximumChange), tolerance, false);
  }

  public static EventFunctionSpecification decrease(double minimumChange, double maximumChange, double tolerance) {
    return decrease(String.valueOf(minimumChange), BuilderUtil.monotonicUpperBound(maximumChange), String.valueOf(tolerance), false);
  }
}
