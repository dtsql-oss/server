package org.tsdl.implementation.math.impl.model;

import org.tsdl.implementation.math.model.LinearModel;

/**
 * Default implementation of {@link LinearModel}.
 */
public record LinearModelImpl(double slope, double ordinateIntercept) implements LinearModel {
}
