package org.tsdl.implementation.evaluation.impl.sample.aggregation.temporal;

import java.time.Instant;
import org.tsdl.implementation.model.sample.aggregation.temporal.TimePeriod;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.common.TsdlTimeUnit;
import org.tsdl.infrastructure.common.TsdlUtil;

/**
 * Default implementation of {@link TimePeriod}.
 */
public record TimePeriodImpl(Instant start, Instant end) implements TimePeriod {

  public TimePeriodImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, start, "Period start must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, end, "Period end must not be null.");
    Conditions.checkIsFalse(Condition.ARGUMENT, start.isAfter(end), "Period start must not be after end.");
  }

  @Override
  public double duration(TsdlTimeUnit unit) {
    return TsdlUtil.getTimespan(start, end, unit);
  }
}
