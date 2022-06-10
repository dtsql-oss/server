package org.tsdl.infrastructure.model;

import java.time.Instant;

/**
 * A result of the evaluation process of a TSDL query that may be part of a {@link TsdlPeriodSet} instance or a standalone result by itself.
 */
public interface TsdlPeriod extends QueryResult {
  TsdlPeriod EMPTY = QueryResult.of(null, null, null);

  Integer index();

  Instant start();

  Instant end();

  boolean isEmpty();

  @Override
  default QueryResultType type() {
    return QueryResultType.PERIOD;
  }
}
