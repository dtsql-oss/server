package org.tsdl.infrastructure.model;

import java.util.List;

/**
 * A result of the evaluation process of a TSDL query that consists of multiple {@link TsdlPeriod} instances..
 */
public interface TsdlPeriods extends QueryResult {

  int totalPeriods();

  List<TsdlPeriod> periods();

  @Override
  default QueryResultType type() {
    return QueryResultType.PERIODS;
  }
}
