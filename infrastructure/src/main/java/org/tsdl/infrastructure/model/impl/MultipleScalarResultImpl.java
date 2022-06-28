package org.tsdl.infrastructure.model.impl;

import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
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
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode
@ToString
public final class MultipleScalarResultImpl implements MultipleScalarResult {
  private final List<Double> values;
  private final List<TsdlLogEvent> logs;

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
