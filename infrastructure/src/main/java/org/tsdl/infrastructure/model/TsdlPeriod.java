package org.tsdl.infrastructure.model;

import java.time.Instant;
import org.tsdl.infrastructure.model.impl.TsdlPeriodImpl;

/**
 * A result of the evaluation process of a TSDL query that may be part of a {@link TsdlPeriodSet} instance or a standalone result by itself.
 */
public interface TsdlPeriod extends QueryResult {
  TsdlPeriod EMPTY = new TsdlPeriodImpl();

  Integer index();

  Instant start();

  Instant end();

  boolean isEmpty();

  @Override
  default QueryResultType type() {
    return QueryResultType.PERIOD;
  }
}
