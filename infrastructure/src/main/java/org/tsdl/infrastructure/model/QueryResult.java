package org.tsdl.infrastructure.model;

import java.time.Instant;
import java.util.List;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.impl.TsdlDataPointsImpl;
import org.tsdl.infrastructure.model.impl.TsdlPeriodImpl;
import org.tsdl.infrastructure.model.impl.TsdlPeriodsImpl;

/**
 * A result of the evaluation process of a TSDL query.
 */
public interface QueryResult {

  QueryResultType type();

  static TsdlDataPoints of(List<DataPoint> items) {
    Conditions.checkNotNull(Condition.ARGUMENT, items, "Data point list must not be null.");
    return new TsdlDataPointsImpl(items);
  }

  static TsdlPeriod of(int index, Instant start, Instant end) {
    Conditions.checkNotNull(Condition.ARGUMENT, start, "Period start must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, end, "Period end must not be null.");

    return new TsdlPeriodImpl(index, start, end);
  }

  static TsdlPeriods of(int totalPeriods, List<TsdlPeriod> periods) {
    Conditions.checkNotNull(Condition.ARGUMENT, periods, "Period list must not be null.");
    Conditions.checkEquals(Condition.ARGUMENT, totalPeriods, periods.size(), "Argument 'totalPeriods' must be equal to the size of 'periods'.");
    return new TsdlPeriodsImpl(totalPeriods, periods);
  }
}
