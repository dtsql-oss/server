package org.tsdl.implementation.evaluation.impl.choice;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.tsdl.implementation.model.choice.AnnotatedTsdlPeriod;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.TsdlPeriod;

/**
 * Default implementation of {@link AnnotatedTsdlPeriod}.
 */
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode
@ToString
public final class AnnotatedTsdlPeriodImpl implements AnnotatedTsdlPeriod {
  private final TsdlPeriod period;
  private final TsdlIdentifier event;

  public AnnotatedTsdlPeriodImpl(TsdlPeriod period, TsdlIdentifier event) {
    Conditions.checkNotNull(Condition.ARGUMENT, period, "Period referenced by AnnotatedPeriod must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, event, "Identifier of event referenced by AnnotatedPeriod must not be null.");
    this.period = period;
    this.event = event;
  }
}
