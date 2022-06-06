package org.tsdl.infrastructure.model.impl;

import java.util.List;
import org.tsdl.infrastructure.model.TsdlPeriod;
import org.tsdl.infrastructure.model.TsdlPeriods;

/**
 * Default implementation of the {@link TsdlPeriods} interface.
 */
public record TsdlPeriodsImpl(int totalPeriods, List<TsdlPeriod> periods) implements TsdlPeriods {
}
