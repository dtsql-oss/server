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
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.infrastructure.model.TsdlDataPoints;
import org.tsdl.infrastructure.model.TsdlLogEvent;

/**
 * Default implementation of the {@link TsdlDataPoints} interface.
 *
 */
@Jacksonized
@Builder
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode
@ToString
public final class TsdlDataPointsImpl implements TsdlDataPoints {
  private final List<DataPoint> items;
  private final List<TsdlLogEvent> logs;

  public TsdlDataPointsImpl(List<DataPoint> items, List<TsdlLogEvent> logs) {
    Conditions.checkNotNull(Condition.ARGUMENT, items, "Items must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, logs, "Logs must not be null.");
    this.items = items;
    this.logs = logs;
  }

  @Override
  public QueryResult withLogs(List<TsdlLogEvent> logs) {
    return new TsdlDataPointsImpl(items, logs);
  }
}
