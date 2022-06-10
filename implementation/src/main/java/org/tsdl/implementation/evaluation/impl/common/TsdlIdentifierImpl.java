package org.tsdl.implementation.evaluation.impl.common;

import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

/**
 * Default implementation of {@link TsdlIdentifier}.
 */
public record TsdlIdentifierImpl(String name) implements TsdlIdentifier {
  public TsdlIdentifierImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, name, "Name of identifier must not be null.");
    Conditions.checkIsFalse(Condition.ARGUMENT, name.trim().length() == 0, "Name of identifier must not be blank.");
  }
}
