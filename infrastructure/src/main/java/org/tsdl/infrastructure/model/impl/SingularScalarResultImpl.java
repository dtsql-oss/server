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
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.infrastructure.model.SingularScalarResult;
import org.tsdl.infrastructure.model.TsdlLogEvent;

/**
 * Default implementation of {@link SingularScalarResult}.
 */
@Jacksonized
@Builder
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode
@ToString
public final class SingularScalarResultImpl implements SingularScalarResult {
  private final Double value;
  private final List<TsdlLogEvent> logs;

  public SingularScalarResultImpl(Double value, List<TsdlLogEvent> logs) {
    Conditions.checkNotNull(Condition.ARGUMENT, value, "Value must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, logs, "Logs must not be null.");
    this.value = value;
    this.logs = logs;
  }

  @Override
  public QueryResult withLogs(List<TsdlLogEvent> logs) {
    return new SingularScalarResultImpl(value, logs);
  }
}
