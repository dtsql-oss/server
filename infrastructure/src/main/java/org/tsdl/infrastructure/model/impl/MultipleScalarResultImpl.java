package org.tsdl.infrastructure.model.impl;

import java.util.List;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.MultipleScalarResult;

/**
 * Default implementation of {@link MultipleScalarResult}.
 */
public record MultipleScalarResultImpl(List<Double> values) implements MultipleScalarResult {
  public MultipleScalarResultImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, values, "Values must not be null.");
  }
}
