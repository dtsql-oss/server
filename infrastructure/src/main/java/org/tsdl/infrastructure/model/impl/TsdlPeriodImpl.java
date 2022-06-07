package org.tsdl.infrastructure.model.impl;

import java.time.Instant;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.infrastructure.model.TsdlPeriod;

/**
 * Default implementation of the {@link TsdlPeriod} interface.
 */
public record TsdlPeriodImpl(int index, Instant start, Instant end) implements TsdlPeriod {
  @Override
  public TsdlPeriod withIndex(int index) {
    return QueryResult.of(index, this.start, this.end);
  }
}
