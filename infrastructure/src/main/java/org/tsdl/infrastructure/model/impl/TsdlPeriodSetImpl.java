package org.tsdl.infrastructure.model.impl;

import java.util.List;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.TsdlPeriod;
import org.tsdl.infrastructure.model.TsdlPeriodSet;

/**
 * Default implementation of the {@link TsdlPeriodSet} interface.
 */
public record TsdlPeriodSetImpl(int totalPeriods, List<TsdlPeriod> periods) implements TsdlPeriodSet {
  public TsdlPeriodSetImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, periods, "Period list must not be null.");
    Conditions.checkEquals(Condition.ARGUMENT, totalPeriods, periods.size(), "Argument 'totalPeriods' must be equal to the size of 'periods'.");
  }
}
