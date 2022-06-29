package org.tsdl.infrastructure.model.impl;

import java.util.List;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.infrastructure.model.TsdlLogEvent;
import org.tsdl.infrastructure.model.TsdlPeriod;
import org.tsdl.infrastructure.model.TsdlPeriodSet;

/**
 * Default implementation of the {@link TsdlPeriodSet} interface.
 */
@Jacksonized
@Builder
@Value
@Accessors(fluent = true)
public class TsdlPeriodSetImpl implements TsdlPeriodSet {
  int totalPeriods;
  List<TsdlPeriod> periods;
  List<TsdlLogEvent> logs;

  /**
   * Initializes a {@link TsdlPeriodSetImpl} instance.
   */
  public TsdlPeriodSetImpl(int totalPeriods, List<TsdlPeriod> periods, List<TsdlLogEvent> logs) {
    Conditions.checkNotNull(Condition.ARGUMENT, periods, "Period list must not be null.");
    Conditions.checkEquals(Condition.ARGUMENT, totalPeriods, periods.size(), "Argument 'totalPeriods' must be equal to the size of 'periods'.");
    Conditions.checkNotNull(Condition.ARGUMENT, logs, "Logs must not be null.");
    this.totalPeriods = totalPeriods;
    this.periods = periods;
    this.logs = logs;
  }

  @Override
  public boolean isEmpty() {
    return totalPeriods == 0 && periods.isEmpty();
  }

  @Override
  public QueryResult withLogs(List<TsdlLogEvent> logs) {
    return new TsdlPeriodSetImpl(totalPeriods, periods, logs);
  }
}
