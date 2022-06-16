package org.tsdl.infrastructure.model;

/**
 * A TSDL query result that consists of a single value.
 */
public interface SingularScalarResult extends QueryResult {
  Double value();

  @Override
  default QueryResultType type() {
    return QueryResultType.SCALAR;
  }
}
