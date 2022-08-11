package org.tsdl.implementation.evaluation.impl.event.strategy;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
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

// TODO test strategies, e.g.:
//  WITH SAMPLES: avg() AS myAvg -> echo(2), avg(\"2022-07-05T23:55:00Z\", \"2022-11-12T23:59:00Z\") AS myLocalAvg -> echo(5)
//  USING EVENTS: AND(lt(myAvg)) FOR (45,) minutes AS low, AND(gt(myAvg)) FOR (100,) minutes AS high  CHOOSE: high follows low    YIELD: all periods
//  results in AnnotatedPeriods: {high=[2022-12-15T04:51:48Z-2022-12-15T07:51:48Z],
//                                low=[2022-12-15T01:21:48Z-2022-12-15T02:36:48Z, 2022-12-15T08:06:48Z-2022-12-15T09:21:48Z]}
//  => assert period boundaries as well as prior and subsequent data points

/**
 * Default implementation of {@link SinglePointEventStrategy}.
 */
@Slf4j
public class SinglePointEventStrategyImpl implements SinglePointEventStrategy {
  @Override
  public List<AnnotatedTsdlPeriod> detectPeriods(List<DataPoint> dataPoints, List<TsdlEventDefinition> events) {
    log.debug("Detecting periods using '{}' over {} data points and {} events.", SinglePointEventStrategyImpl.class.getName(), dataPoints.size(),
        events.size());
    var eventMarkers = new HashMap<TsdlIdentifier, Instant>();
    var priorDataPoints = new HashMap<TsdlIdentifier, DataPoint>();
    var detectedPeriods = new ArrayList<AnnotatedTsdlPeriod>();

    for (var i = 0; i < dataPoints.size(); i++) {
      var currentDataPoint = dataPoints.get(i);
      var previousDataPoint = i > 0 ? dataPoints.get(i - 1) : null;
      var nextDataPoint = i < dataPoints.size() - 1 ? dataPoints.get(i + 1) : null;

      markEvents(events, eventMarkers, priorDataPoints, detectedPeriods, currentDataPoint, previousDataPoint, nextDataPoint);
    }

    log.debug("Detected {} periods using '{}'.", detectedPeriods.size(), SinglePointEventStrategyImpl.class.getName());
    return Collections.unmodifiableList(detectedPeriods);
  }

  private void markEvents(List<TsdlEventDefinition> events, Map<TsdlIdentifier, Instant> eventMarkers, Map<TsdlIdentifier, DataPoint> priorDataPoints,
                          ArrayList<AnnotatedTsdlPeriod> detectedPeriods, DataPoint currentDataPoint, DataPoint previousDataPoint,
                          DataPoint nextDataPoint) {
    for (TsdlEventDefinition event : events) {
      if (!(event instanceof SinglePointEventDefinition eventDef)) {
        throw Conditions.exception(Condition.ARGUMENT, "The event strategy '%s' only supports event definitions of type '%s'. Received: '%s'",
            getClass().getName(), SinglePointEventDefinition.class.getName(), event.getClass().getName());
      }

      markEvent(currentDataPoint, previousDataPoint, nextDataPoint, eventDef, eventMarkers, priorDataPoints, detectedPeriods);
    }
  }


  private void markEvent(DataPoint currentDataPoint, DataPoint previousDataPoint, DataPoint nextDataPoint, SinglePointEventDefinition eventDefinition,
                         Map<TsdlIdentifier, Instant> eventMarkers, Map<TsdlIdentifier, DataPoint> priorDataPoints,
                         List<AnnotatedTsdlPeriod> detectedPeriods) {
    if (eventDefinition.connective().isSatisfied(currentDataPoint)) {
      // satisfied - either period is still going on or the period starts

      if (eventMarkers.containsKey(eventDefinition.identifier())) {
        // period is still going on

        if (nextDataPoint == null) {
          // if the end of the data is reached, the period must end, too
          finalizePeriod(detectedPeriods, eventMarkers, priorDataPoints, eventDefinition.identifier(), currentDataPoint.timestamp(), null);
        }
      } else {
        // new period starts
        eventMarkers.put(eventDefinition.identifier(), currentDataPoint.timestamp());
        priorDataPoints.put(eventDefinition.identifier(), previousDataPoint);
      }

    } else {
      // not satisfied - either period ends, or there is still no open period

      //noinspection StatementWithEmptyBody
      if (eventMarkers.containsKey(eventDefinition.identifier())) {
        // period ended
        Conditions.checkNotNull(Condition.STATE, previousDataPoint, "When closing a period, there must be a previous data point.");
        finalizePeriod(detectedPeriods, eventMarkers, priorDataPoints, eventDefinition.identifier(), previousDataPoint.timestamp(), currentDataPoint);
      } else {
        // nothing to do - still no open period
      }
    }
  }

  private void finalizePeriod(List<AnnotatedTsdlPeriod> periods, Map<TsdlIdentifier, Instant> eventMarkers,
                              Map<TsdlIdentifier, DataPoint> priorDataPoints, TsdlIdentifier eventId, Instant periodEnd,
                              DataPoint subsequentDataPoint) {
    var finalizedPeriod = QueryResult.of(-1, eventMarkers.get(eventId), periodEnd);
    periods.add(new AnnotatedTsdlPeriodImpl(finalizedPeriod, eventId, priorDataPoints.get(eventId), subsequentDataPoint));
    eventMarkers.remove(eventId);
    priorDataPoints.remove(eventId);
  }
}
