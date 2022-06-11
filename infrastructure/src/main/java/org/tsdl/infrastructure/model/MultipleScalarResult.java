package org.tsdl.infrastructure.model;

import java.util.List;

/**
 * A TSDL query result that consists of multiple values.
 */
public interface MultipleScalarResult extends QueryResult {
  List<Double> values();

  @Override
  default QueryResultType type() {
    return QueryResultType.SCALAR_LIST;
  }
}
