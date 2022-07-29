package org.tsdl.implementation.evaluation.impl;

import java.util.List;
import org.tsdl.implementation.model.choice.AnnotatedTsdlPeriod;
import org.tsdl.implementation.model.event.TsdlEvent;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Provides functionality to assemble all periods specified by the events of a query.
 */
public interface TsdlPeriodAssembler {
  /**
   * Precondition: SampleFilterArgument values have been set.
   * Postcondition: detected periods are ordered by start time;
   * for equal start times, the period whose declaring event has the lower index has precedence
   */
  List<AnnotatedTsdlPeriod> assemble(List<DataPoint> dataPoints, List<TsdlEvent> events);
}
