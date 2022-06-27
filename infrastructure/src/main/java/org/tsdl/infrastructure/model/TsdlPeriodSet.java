package org.tsdl.infrastructure.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.tsdl.infrastructure.model.impl.TsdlPeriodSetImpl;

/**
 * A result of the evaluation process of a TSDL query that consists of multiple {@link TsdlPeriod} instances..
 */
@JsonDeserialize(as = TsdlPeriodSetImpl.class)
public interface TsdlPeriodSet extends QueryResult {
  TsdlPeriodSet EMPTY = QueryResult.of(0, List.of());

  int totalPeriods();

  List<TsdlPeriod> periods();

  boolean isEmpty();

  @Override
  default QueryResultType type() {
    return QueryResultType.PERIOD_SET;
  }
}
