package org.tsdl.implementation.evaluation.impl.event.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.tsdl.implementation.model.choice.AnnotatedTsdlPeriod;
import org.tsdl.implementation.model.common.TsdlDuration;
import org.tsdl.implementation.model.event.TsdlEvent;
import org.tsdl.implementation.model.event.strategy.DurationEventStrategy;
import org.tsdl.implementation.model.event.strategy.TsdlEventStrategy;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.infrastructure.model.TsdlPeriod;

/**
 * Default implementation of {@link DurationEventStrategy}.
 */
@Slf4j
public record DurationEventStrategyImpl(TsdlEventStrategy strategy) implements DurationEventStrategy {
  @Override
  public List<AnnotatedTsdlPeriod> detectPeriods(List<DataPoint> dataPoints, List<TsdlEvent> events) {
    log.debug("Detecting periods using composite strategy '{}' over {} data points and {} events.", DurationEventStrategyImpl.class.getName(),
        dataPoints.size(),
        events.size());

    var eventsByIdentifier = events.stream()
        .collect(Collectors.toMap(TsdlEvent::identifier, Function.identity()));
    log.debug("Employing wrapped strategy '{}' to assemble initial periods.", strategy.getClass().getName());
    var detectedPeriods = strategy.detectPeriods(dataPoints, events);
    log.debug("Detected {} data points using wrapped strategy '{}'. Filtering the result now using duration-specific logic.", detectedPeriods.size(),
        strategy.getClass().getName());

    var validPeriods = new ArrayList<AnnotatedTsdlPeriod>();
    for (var detectedPeriod : detectedPeriods) {
      if (!eventsByIdentifier.containsKey(detectedPeriod.event())) {
        throw Conditions.exception(Condition.STATE, "Cannot reconstruct event definition from identifier '%s'", detectedPeriod.event().name());
      }

      var eventDefinition = eventsByIdentifier.get(detectedPeriod.event());
      if (eventDefinition.duration().isEmpty()) {
        throw Conditions.exception(Condition.STATE, "In order to verify duration constraints, an event duration must be present.");
      }

      if (satisfiesDurationConstraint(detectedPeriod.period(), eventDefinition.duration().get())) {
        validPeriods.add(detectedPeriod);
      }
    }

    log.debug("Detected {} valid periods using composite strategy '{}'.", validPeriods.size(), DurationEventStrategyImpl.class.getName());
    return Collections.unmodifiableList(validPeriods);
  }

  private boolean satisfiesDurationConstraint(TsdlPeriod period, TsdlDuration duration) {
    var unitAdjustedDuration = period.duration(duration.unit().modelEquivalent());
    return duration.isSatisfiedBy(unitAdjustedDuration);
  }
}
