package org.tsdl.infrastructure.model;

import java.time.Instant;

/**
 * A result of the evaluation process of a TSDL query that may be part of a {@link TsdlPeriods} instance or a standalone result by itself.
 */
public interface TsdlPeriod extends QueryResult {

  int index();

  Instant start();

  Instant end();

  @Override
  default QueryResultType type() {
    return QueryResultType.PERIOD;
  }
}
