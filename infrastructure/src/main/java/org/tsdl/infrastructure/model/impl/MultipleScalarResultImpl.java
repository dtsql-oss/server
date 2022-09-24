package org.tsdl.infrastructure.model.impl;

import java.util.List;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.MultipleScalarResult;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.infrastructure.model.TsdlLogEvent;

/**
 * Default implementation of {@link MultipleScalarResult}.
 */
public record MultipleScalarResultImpl(List<Double> values, List<TsdlLogEvent> logs) implements MultipleScalarResult {
  public MultipleScalarResultImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, values, "Values must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, logs, "Logs must not be null.");
  }

  @Override
  public QueryResult withLogs(List<TsdlLogEvent> logs) {
    return new MultipleScalarResultImpl(values, logs);
  }
}
