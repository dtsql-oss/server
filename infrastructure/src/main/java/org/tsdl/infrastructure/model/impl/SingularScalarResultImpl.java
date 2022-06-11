package org.tsdl.infrastructure.model.impl;

import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.SingularScalarResult;

/**
 * Default implementation of {@link SingularScalarResult}.
 */
public record SingularScalarResultImpl(Double value) implements SingularScalarResult {
  public SingularScalarResultImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, value, "Value must not be null.");
  }
}
