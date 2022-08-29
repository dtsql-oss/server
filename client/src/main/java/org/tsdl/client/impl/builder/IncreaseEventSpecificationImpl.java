package org.tsdl.client.impl.builder;

import org.tsdl.client.api.builder.ComplexEventFunctionSpecification.MonotonicEventSpecification;
import org.tsdl.client.api.builder.EventFunctionSpecification;

/**
 * Default implementation of {@link MonotonicEventSpecification} for type {@link MonotonicEventType#INCREASE}.
 */
public class IncreaseEventSpecificationImpl implements MonotonicEventSpecification {
  private final String minimumChange;
  private final String maximumChange;
  private final String tolerance;
  private final boolean negated;

  private IncreaseEventSpecificationImpl(String minimumChange, String maximumChange, String tolerance, boolean negated) {
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
    return MonotonicEventType.INCREASE;
  }

  @Override
  public boolean isNegated() {
    return negated;
  }

  @Override
  public EventFunctionSpecification negate() {
    return new IncreaseEventSpecificationImpl(minimumChange, maximumChange, tolerance, !negated);
  }

  public static EventFunctionSpecification increase(String minimumChange, String maximumChange, String tolerance, boolean negated) {
    return new IncreaseEventSpecificationImpl(minimumChange, maximumChange, tolerance, negated);
  }

  public static EventFunctionSpecification increase(String minimumChange, String maximumChange, double tolerance, boolean negated) {
    return increase(minimumChange, maximumChange, String.valueOf(tolerance), negated);
  }

  public static EventFunctionSpecification increase(String minimumChange, double maximumChange, String tolerance, boolean negated) {
    return increase(minimumChange, BuilderUtil.monotonicUpperBound(maximumChange), tolerance, negated);
  }

  public static EventFunctionSpecification increase(String minimumChange, double maximumChange, double tolerance, boolean negated) {
    return increase(minimumChange, BuilderUtil.monotonicUpperBound(maximumChange), String.valueOf(tolerance), negated);
  }

  public static EventFunctionSpecification increase(double minimumChange, String maximumChange, String tolerance, boolean negated) {
    return increase(String.valueOf(minimumChange), maximumChange, tolerance, negated);
  }

  public static EventFunctionSpecification increase(double minimumChange, String maximumChange, double tolerance, boolean negated) {
    return increase(String.valueOf(minimumChange), maximumChange, String.valueOf(tolerance), negated);
  }

  public static EventFunctionSpecification increase(double minimumChange, double maximumChange, String tolerance, boolean negated) {
    return increase(String.valueOf(minimumChange), BuilderUtil.monotonicUpperBound(maximumChange), tolerance, negated);
  }

  public static EventFunctionSpecification increase(double minimumChange, double maximumChange, double tolerance, boolean negated) {
    return increase(String.valueOf(minimumChange), BuilderUtil.monotonicUpperBound(maximumChange), String.valueOf(tolerance), negated);
  }

  public static EventFunctionSpecification increase(String minimumChange, String maximumChange, String tolerance) {
    return increase(minimumChange, maximumChange, tolerance, false);
  }

  public static EventFunctionSpecification increase(String minimumChange, String maximumChange, double tolerance) {
    return increase(minimumChange, maximumChange, String.valueOf(tolerance), false);
  }

  public static EventFunctionSpecification increase(String minimumChange, double maximumChange, String tolerance) {
    return increase(minimumChange, BuilderUtil.monotonicUpperBound(maximumChange), tolerance, false);
  }

  public static EventFunctionSpecification increase(String minimumChange, double maximumChange, double tolerance) {
    return increase(minimumChange, BuilderUtil.monotonicUpperBound(maximumChange), String.valueOf(tolerance), false);
  }

  public static EventFunctionSpecification increase(double minimumChange, String maximumChange, String tolerance) {
    return increase(String.valueOf(minimumChange), maximumChange, tolerance, false);
  }

  public static EventFunctionSpecification increase(double minimumChange, String maximumChange, double tolerance) {
    return increase(String.valueOf(minimumChange), maximumChange, String.valueOf(tolerance), false);
  }

  public static EventFunctionSpecification increase(double minimumChange, double maximumChange, String tolerance) {
    return increase(String.valueOf(minimumChange), BuilderUtil.monotonicUpperBound(maximumChange), tolerance, false);
  }

  public static EventFunctionSpecification increase(double minimumChange, double maximumChange, double tolerance) {
    return increase(String.valueOf(minimumChange), BuilderUtil.monotonicUpperBound(maximumChange), String.valueOf(tolerance), false);
  }
}
