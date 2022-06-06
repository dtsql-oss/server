package org.tsdl.infrastructure.model;

import java.util.List;

/**
 * A result of the evaluation process of a TSDL query that consists of multiple {@link DataPoint} instances.
 */
public interface TsdlDataPoints extends QueryResult {
  List<DataPoint> items();

  @Override
  default QueryResultType type() {
    return QueryResultType.DATA_POINTS;
  }
}
