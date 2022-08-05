package org.tsdl.implementation.evaluation.impl.filter.threshold.argument;

import org.tsdl.implementation.model.filter.threshold.argument.TsdlLiteralFilterArgument;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

/**
 * Default implementation of {@link TsdlLiteralFilterArgument}.
 */
public record TsdlLiteralFilterArgumentImpl(double value) implements TsdlLiteralFilterArgument {
  public TsdlLiteralFilterArgumentImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, value, "Value of literal filter argument must not be null.");
  }
}
