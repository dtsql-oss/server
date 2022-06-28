package org.tsdl.infrastructure.model.impl;

import java.time.Instant;
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
import org.tsdl.infrastructure.model.TsdlLogEvent;
import org.tsdl.infrastructure.model.TsdlPeriod;

/**
 * Default implementation of the {@link TsdlPeriod} interface.
 */
@Jacksonized
@Builder
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode
@ToString
public final class TsdlPeriodImpl implements TsdlPeriod {
  private final Integer index;
  private final Instant start;
  private final Instant end;
  private final List<TsdlLogEvent> logs;

  /**
   * Create an {@link TsdlPeriodSetImpl} instance. Either all parameters have to be null (being equivalent to {@link TsdlPeriod#EMPTY}, or
   * at least the {@link Instant} arguments {@code start} and {@code end}. The {@code index} parameter may be null because initializing a
   * {@link TsdlPeriod} with unknown index (because it becomes known/computed only later on) is allowed.
   */
  public TsdlPeriodImpl(Integer index, Instant start, Instant end, List<TsdlLogEvent> logs) {
    if (!emptyData(index, start, end)) {
      if (index != null) {
        // initializing with unknown index is okay (e.g. if it is known/calculated only later on)
        Conditions.checkNotNull(Condition.ARGUMENT, index, "Period index must not be null.");
        Conditions.checkIsGreaterThanOrEqual(Condition.ARGUMENT, index, 0, "Index must be greater than or equal to zero.");
      }

      Conditions.checkNotNull(Condition.ARGUMENT, start, "Period start must not be null.");
      Conditions.checkNotNull(Condition.ARGUMENT, end, "Period end must not be null.");
    }

    Conditions.checkNotNull(Condition.ARGUMENT, logs, "Logs must not be null.");
    this.index = index;
    this.start = start;
    this.end = end;
    this.logs = logs;
  }

  @Override
  public boolean isEmpty() {
    return emptyData(index, start, end);
  }

  @Override
  public QueryResult withLogs(List<TsdlLogEvent> logs) {
    return new TsdlPeriodImpl(index, start, end, logs);
  }

  private static boolean emptyData(Integer index, Instant start, Instant end) {
    return index == null && start == null && end == null;
  }
}
