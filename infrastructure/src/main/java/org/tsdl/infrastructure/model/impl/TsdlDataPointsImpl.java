package org.tsdl.infrastructure.model.impl;

import java.util.List;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.infrastructure.model.TsdlDataPoints;
import org.tsdl.infrastructure.model.TsdlLogEvent;

/**
 * Default implementation of the {@link TsdlDataPoints} interface.
 */
public record TsdlDataPointsImpl(List<DataPoint> items, List<TsdlLogEvent> logs) implements TsdlDataPoints {
  public TsdlDataPointsImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, items, "Items must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, logs, "Logs must not be null.");
  }

  @Override
  public QueryResult withLogs(List<TsdlLogEvent> logs) {
    return new TsdlDataPointsImpl(items, logs);
  }
}
