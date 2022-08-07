package org.tsdl.infrastructure.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import org.tsdl.infrastructure.common.TsdlTimeUnit;
import org.tsdl.infrastructure.model.impl.TsdlPeriodImpl;

/**
 * A result of the evaluation process of a TSDL query that may be part of a {@link TsdlPeriodSet} instance or a standalone result by itself.
 */
@JsonDeserialize(as = TsdlPeriodImpl.class)
public interface TsdlPeriod extends QueryResult {
  TsdlPeriod EMPTY = QueryResult.of(-1, null, null, new TsdlLogEvent[0]);

  int index();

  Instant start();

  Instant end();

  boolean isEmpty();

  boolean contains(Instant timestamp);

  double duration(TsdlTimeUnit unit);

  @Override
  default QueryResultType type() {
    return QueryResultType.PERIOD;
  }
}
