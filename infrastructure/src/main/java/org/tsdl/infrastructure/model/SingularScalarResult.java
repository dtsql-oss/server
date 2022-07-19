package org.tsdl.infrastructure.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.tsdl.infrastructure.model.impl.SingularScalarResultImpl;

/**
 * A TSDL query result that consists of a single value.
 */
@JsonDeserialize(as = SingularScalarResultImpl.class)
public interface SingularScalarResult extends QueryResult {
  Double value();

  @Override
  default QueryResultType type() {
    return QueryResultType.SCALAR;
  }
}
