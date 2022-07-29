package org.tsdl.implementation.evaluation.impl.event.strategy;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.tsdl.implementation.evaluation.impl.choice.AnnotatedTsdlPeriodImpl;
import org.tsdl.implementation.model.choice.AnnotatedTsdlPeriod;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.event.definition.SinglePointEventDefinition;
import org.tsdl.implementation.model.event.definition.TsdlEventDefinition;
import org.tsdl.implementation.model.event.strategy.SinglePointEventStrategy;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.infrastructure.model.QueryResult;

/**
 * Default implementation of {@link SinglePointEventStrategy}.
 */
public class SinglePointEventStrategyImpl implements SinglePointEventStrategy {
  @Override
  public List<AnnotatedTsdlPeriod> detectPeriods(List<DataPoint> dataPoints, List<TsdlEventDefinition> events) {
    var eventMarkers = new HashMap<TsdlIdentifier, Instant>();
    var detectedPeriods = new ArrayList<AnnotatedTsdlPeriod>();
    for (var i = 0; i < dataPoints.size(); i++) {
      var currentDataPoint = dataPoints.get(i);
      var previousDataPoint = i > 0 ? dataPoints.get(i - 1) : null;

      markEvents(dataPoints, events, eventMarkers, detectedPeriods, i, currentDataPoint, previousDataPoint);
    }

    return detectedPeriods;
  }

  private void markEvents(List<DataPoint> dataPoints, List<TsdlEventDefinition> events, HashMap<TsdlIdentifier, Instant> eventMarkers,
                         ArrayList<AnnotatedTsdlPeriod> detectedPeriods, int i, DataPoint currentDataPoint, DataPoint previousDataPoint) {
    for (TsdlEventDefinition event : events) {
      if (!(event instanceof SinglePointEventDefinition eventDef)) {
        throw Conditions.exception(Condition.ARGUMENT, "The event strategy '%s' only supports event definitions of type '%s'. Received: '%s'",
            getClass().getName(), SinglePointEventDefinition.class.getName(), event.getClass().getName());
      }

      markEvent(currentDataPoint, previousDataPoint, i == dataPoints.size() - 1, eventDef, eventMarkers, detectedPeriods);
    }
  }


  private void markEvent(DataPoint currentDataPoint, DataPoint previousDataPoint, boolean isLastDataPoint,
                         SinglePointEventDefinition eventDefinition, HashMap<TsdlIdentifier, Instant> eventMarkers,
                         ArrayList<AnnotatedTsdlPeriod> detectedPeriods) {
    if (eventDefinition.connective().isSatisfied(currentDataPoint)) {
      // satisfied - either period is still going on or the period starts

      if (eventMarkers.containsKey(eventDefinition.identifier())) {
        // period is still going on

        if (isLastDataPoint) {
          // if the end of the data is reached, the period must end, too
          finalizePeriod(detectedPeriods, eventMarkers, eventDefinition.identifier(), currentDataPoint.timestamp());
        }
      } else {
        // new period starts
        eventMarkers.put(eventDefinition.identifier(), currentDataPoint.timestamp());
      }

    } else {
      // not satisfied - either period ends, or there is still no open period

      //noinspection StatementWithEmptyBody
      if (eventMarkers.containsKey(eventDefinition.identifier())) {
        // period ended
        Conditions.checkNotNull(Condition.STATE, previousDataPoint, "When closing a period, there must be a previous data point.");
        finalizePeriod(detectedPeriods, eventMarkers, eventDefinition.identifier(), previousDataPoint.timestamp());
      } else {
        // nothing to do - still no open period
      }
    }
  }

  private void finalizePeriod(List<AnnotatedTsdlPeriod> periods, Map<TsdlIdentifier, Instant> eventMarkers, TsdlIdentifier eventId,
                              Instant periodEnd) {
    var finalizedPeriod = QueryResult.of(null, eventMarkers.get(eventId), periodEnd);
    periods.add(new AnnotatedTsdlPeriodImpl(finalizedPeriod, eventId));
    eventMarkers.remove(eventId);
  }
}
