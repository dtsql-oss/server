package org.tsdl.implementation.math.model;

/**
 * <p>
 * Represents a linear function of the form:
 * </p>
 * <pre>
 *   f(x) = a * x + b
 * </pre>
 * .
 * <p>
 * In this context, {@code a} is the slope of the line represented by {@code f}, and {@code b} is the y-intercept (ordinate intercept).
 * </p>
 */
public interface LinearModel {
  double slope();

  double ordinateIntercept();
}
