package org.tsdl.infrastructure.model.impl;

import java.util.List;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.infrastructure.model.SingularScalarResult;
import org.tsdl.infrastructure.model.TsdlLogEvent;

/**
 * Default implementation of {@link SingularScalarResult}.
 */
public record SingularScalarResultImpl(Double value, List<TsdlLogEvent> logs) implements SingularScalarResult {
  public SingularScalarResultImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, value, "Value must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, logs, "Logs must not be null.");
  }

  @Override
  public QueryResult withLogs(List<TsdlLogEvent> logs) {
    return new SingularScalarResultImpl(value, logs);
  }
}
