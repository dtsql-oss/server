package org.tsdl.implementation.evaluation.impl.filter.argument;

import org.tsdl.implementation.model.filter.argument.TsdlLiteralScalarArgument;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

/**
 * Default implementation of {@link TsdlLiteralScalarArgument}.
 */
public record TsdlLiteralScalarArgumentImpl(double value) implements TsdlLiteralScalarArgument {
  public TsdlLiteralScalarArgumentImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, value, "Value of literal filter argument must not be null.");
  }
}
