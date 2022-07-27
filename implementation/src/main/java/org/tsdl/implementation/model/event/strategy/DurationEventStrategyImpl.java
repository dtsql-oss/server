package org.tsdl.implementation.model.event.strategy;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.tsdl.implementation.model.choice.AnnotatedTsdlPeriod;
import org.tsdl.implementation.model.event.EventDuration;
import org.tsdl.implementation.model.event.EventDurationUnit;
import org.tsdl.implementation.model.event.definition.TsdlEventDefinition;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.infrastructure.model.TsdlPeriod;

/**
 * Default implementation of {@link DurationEventStrategy}.
 */
public record DurationEventStrategyImpl(TsdlEventStrategy strategy) implements DurationEventStrategy {
  private static final Map<EventDurationUnit, Double> MILLIS_TO_UNIT_CONVERSION_FACTOR = Map.of(
      EventDurationUnit.MILLISECONDS, 1.0,
      EventDurationUnit.SECONDS, 1.0 / 1000,
      EventDurationUnit.MINUTES, 1.0 / (1000 * 60),
      EventDurationUnit.HOURS, 1.0 / (1000 * 60 * 60),
      EventDurationUnit.DAYS, 1.0 / (1000 * 60 * 60 * 24),
      EventDurationUnit.WEEKS, 1.0 / (1000 * 60 * 60 * 24 * 7)
  );

  @Override
  public List<AnnotatedTsdlPeriod> detectPeriods(List<DataPoint> dataPoints, List<TsdlEventDefinition> events) {
    var eventsByIdentifier = events.stream()
        .collect(Collectors.toMap(TsdlEventDefinition::identifier, Function.identity()));
    var detectedPeriods = strategy.detectPeriods(dataPoints, events);

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

    return Collections.unmodifiableList(validPeriods);
  }

  private boolean satisfiesDurationConstraint(TsdlPeriod period, EventDuration duration) {
    var unitAdjustedDuration = getDurationInUnit(period, duration.unit());
    var satisfiesLowerBound = duration.lowerBound().inclusive()
        ? unitAdjustedDuration >= duration.lowerBound().value()
        : unitAdjustedDuration > duration.lowerBound().value();
    var satisfiesUpperBound = duration.upperBound().inclusive()
        ? unitAdjustedDuration <= duration.upperBound().value()
        : unitAdjustedDuration < duration.upperBound().value();

    return satisfiesLowerBound && satisfiesUpperBound;
  }

  private double getDurationInUnit(TsdlPeriod period, EventDurationUnit unit) {
    var durationMillis = ChronoUnit.MILLIS.between(period.start(), period.end());

    if (!MILLIS_TO_UNIT_CONVERSION_FACTOR.containsKey(unit)) {
      throw Conditions.exception(Condition.STATE, "No conversion routine for unit '%s' available.", unit);
    }

    return durationMillis * MILLIS_TO_UNIT_CONVERSION_FACTOR.get(unit);
  }
}
