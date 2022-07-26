package org.tsdl.implementation.model.event.strategy;

import java.util.List;
import org.tsdl.implementation.model.choice.AnnotatedTsdlPeriod;
import org.tsdl.implementation.model.event.TsdlEvent;
import org.tsdl.implementation.model.event.definition.TsdlEventDefinition;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Captures an algorithm for detecting intervals (periods) characterized by event definitions, typically through types derived from
 * {@link TsdlEvent}. This is a manifestation of the strategy pattern in order to extract the actual evaluation of an event from its definition. This
 * also allows one to reuse interval detection procedures for algorithmically equivalent events.
 */
public interface TsdlEventStrategy {
  /**
   * Precondition: SampleFilterArgument values have been set.
   * Postcondition: detected periods are ordered by start time;
   * for equal start times, the period whose declaring event has the lower index has precedence
   */
  List<AnnotatedTsdlPeriod> detectPeriods(List<DataPoint> dataPoints, List<TsdlEventDefinition> events);
}
