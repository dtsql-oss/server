package org.tsdl.infrastructure.model.impl;

import java.time.Instant;
import java.util.List;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.common.TsdlTimeUnit;
import org.tsdl.infrastructure.common.TsdlUtil;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.infrastructure.model.TsdlLogEvent;
import org.tsdl.infrastructure.model.TsdlPeriod;

/**
 * Default implementation of the {@link TsdlPeriod} interface.
 */
public record TsdlPeriodImpl(int index, Instant start, Instant end, List<TsdlLogEvent> logs) implements TsdlPeriod {
  /**
   * Create an {@link TsdlPeriodSetImpl} instance. Either all parameters have to be null (being equivalent to {@link TsdlPeriod#EMPTY}), or
   * at least the {@link Instant} arguments {@code start} and {@code end}. The {@code index} parameter may be null because initializing a
   * {@link TsdlPeriod} with unknown index (because it becomes known/computed only later on) is allowed.
   */
  public TsdlPeriodImpl {
    if (!emptyData(index, start, end)) {
      if (index != -1) {
        // initializing with unknown index is okay (e.g. if it is known/calculated only later on)
        Conditions.checkIsGreaterThanOrEqual(Condition.ARGUMENT, index, 0, "Index must be greater than or equal to zero.");
      }

      Conditions.checkNotNull(Condition.ARGUMENT, start, "Period start must not be null.");
      Conditions.checkNotNull(Condition.ARGUMENT, end, "Period end must not be null.");
      Conditions.checkIsFalse(Condition.ARGUMENT, start.isAfter(end), "Period start must not be after end.");
    }

    Conditions.checkNotNull(Condition.ARGUMENT, logs, "Logs must not be null.");
  }

  @Override
  public boolean isEmpty() {
    return emptyData(index, start, end);
  }

  @Override
  public boolean contains(Instant timestamp) {
    return TsdlUtil.isWithinRange(timestamp, start, end);
  }

  @Override
  public double duration(TsdlTimeUnit unit) {
    return TsdlUtil.getTimespan(start, end, unit);
  }

  @Override
  public TsdlPeriod withIndex(int index) {
    return new TsdlPeriodImpl(index, start, end, logs);
  }

  @Override
  public QueryResult withLogs(List<TsdlLogEvent> logs) {
    return new TsdlPeriodImpl(index, start, end, logs);
  }

  private static boolean emptyData(int index, Instant start, Instant end) {
    return index == -1 && start == null && end == null;
  }
}
