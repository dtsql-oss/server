package org.tsdl.infrastructure.model.impl;

import java.util.List;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.MultipleScalarResult;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.infrastructure.model.TsdlLogEvent;

/**
 * Default implementation of {@link MultipleScalarResult}.
 */
@Jacksonized
@Builder
@Value
@Accessors(fluent = true)
public class MultipleScalarResultImpl implements MultipleScalarResult {
  List<Double> values;
  List<TsdlLogEvent> logs;

  public MultipleScalarResultImpl(List<Double> values, List<TsdlLogEvent> logs) {
    Conditions.checkNotNull(Condition.ARGUMENT, values, "Values must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, logs, "Logs must not be null.");
    this.values = values;
    this.logs = logs;
  }

  @Override
  public QueryResult withLogs(List<TsdlLogEvent> logs) {
    return new MultipleScalarResultImpl(values, logs);
  }
}
