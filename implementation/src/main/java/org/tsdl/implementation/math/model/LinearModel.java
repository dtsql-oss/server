package org.tsdl.implementation.math.model;

import org.tsdl.implementation.math.impl.model.LinearModelImpl;

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

  static LinearModel of(double slope, double ordinateIntercept) {
    return new LinearModelImpl(slope, ordinateIntercept);
  }
}
