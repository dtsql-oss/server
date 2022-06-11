package org.tsdl.infrastructure.model;

import java.time.Instant;
import java.util.List;
import org.tsdl.infrastructure.model.impl.MultipleScalarResultImpl;
import org.tsdl.infrastructure.model.impl.SingularScalarResultImpl;
import org.tsdl.infrastructure.model.impl.TsdlDataPointsImpl;
import org.tsdl.infrastructure.model.impl.TsdlPeriodImpl;
import org.tsdl.infrastructure.model.impl.TsdlPeriodSetImpl;

/**
 * A result of the evaluation process of a TSDL query.
 */
public interface QueryResult {

  QueryResultType type();

  static TsdlDataPoints of(List<DataPoint> items) {
    return new TsdlDataPointsImpl(items);
  }

  static TsdlPeriod of(Integer index, Instant start, Instant end) {
    return new TsdlPeriodImpl(index, start, end);
  }

  static TsdlPeriodSet of(int totalPeriods, List<TsdlPeriod> periods) {
    return new TsdlPeriodSetImpl(totalPeriods, periods);
  }

  static SingularScalarResult of(Double value) {
    return new SingularScalarResultImpl(value);
  }

  static MultipleScalarResult of(Double... values) {
    return new MultipleScalarResultImpl(List.of(values));
  }
}
