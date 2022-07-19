package org.tsdl.implementation.evaluation.impl.filter.argument;

import org.tsdl.implementation.model.filter.argument.TsdlLiteralFilterArgument;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

/**
 * Default implementation of {@link TsdlLiteralFilterArgument}.
 */
public record TsdlLiteralFilterArgumentImpl(Double value) implements TsdlLiteralFilterArgument {
  public TsdlLiteralFilterArgumentImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, value, "Value of literal filter argument must not be null.");
  }
}
