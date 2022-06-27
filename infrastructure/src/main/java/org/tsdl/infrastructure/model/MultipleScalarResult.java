package org.tsdl.infrastructure.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.tsdl.infrastructure.model.impl.MultipleScalarResultImpl;

/**
 * A TSDL query result that consists of multiple values.
 */
@JsonDeserialize(as = MultipleScalarResultImpl.class)
public interface MultipleScalarResult extends QueryResult {
  List<Double> values();

  @Override
  default QueryResultType type() {
    return QueryResultType.SCALAR_LIST;
  }
}
