package org.tsdl.infrastructure.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.tsdl.infrastructure.model.impl.TsdlPeriodSetImpl;

/**
 * A result of the evaluation process of a TSDL query that consists of multiple {@link TsdlPeriod} instances..
 */
@JsonDeserialize(as = TsdlPeriodSetImpl.class)
public interface TsdlPeriodSet extends QueryResult {
  TsdlPeriodSet EMPTY = QueryResult.of(0, List.of());

  @JsonProperty
  int totalPeriods();

  @JsonProperty
  List<TsdlPeriod> periods();

  @JsonProperty
  boolean isEmpty();

  @Override
  default QueryResultType type() {
    return QueryResultType.PERIOD_SET;
  }
}
