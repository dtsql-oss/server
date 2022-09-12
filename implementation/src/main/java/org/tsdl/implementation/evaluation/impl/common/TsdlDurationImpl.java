package org.tsdl.implementation.evaluation.impl.common;

import org.tsdl.implementation.model.common.ParsableTsdlTimeUnit;
import org.tsdl.implementation.model.common.TsdlDuration;
import org.tsdl.implementation.model.common.TsdlDurationBound;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

/**
 * Default implementation of {@link TsdlDuration}.
 */
public record TsdlDurationImpl(
    TsdlDurationBound lowerBound,
    TsdlDurationBound upperBound,
    ParsableTsdlTimeUnit unit
) implements TsdlDuration {

  public TsdlDurationImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, unit, "The unit of the event duration must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, lowerBound, "The lower bound of an event must not be null. Use 0 instead.");
    Conditions.checkNotNull(Condition.ARGUMENT, upperBound, "The upper bound of an event must not be null. Use Long.MAX_VALUE instead.");
  }

  @Override
  public boolean isSatisfiedBy(double unitAdjustedValue) {
    var absoluteValue = Math.abs(unitAdjustedValue);
    var satisfiesLowerBound = lowerBound.inclusive()
        ? absoluteValue >= lowerBound.value()
        : absoluteValue > lowerBound.value();
    var satisfiesUpperBound = upperBound().inclusive()
        ? absoluteValue <= upperBound.value()
        : absoluteValue < upperBound.value();

    return satisfiesLowerBound && satisfiesUpperBound;
  }

  @Override
  public String toString() {
    return "%s%s, %s%s %s".formatted(
        lowerBound.inclusive() ? "[" : "(",
        lowerBound.value(),
        upperBound.value(),
        upperBound.inclusive() ? "]" : ")",
        unit.representation()
    );
  }
}
