package org.tsdl.implementation.evaluation.impl.choice;

import org.tsdl.implementation.model.choice.AnnotatedTsdlPeriod;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.TsdlPeriod;

/**
 * Default implementation of {@link AnnotatedTsdlPeriod}.
 */
public record AnnotatedTsdlPeriodImpl(TsdlPeriod period, TsdlIdentifier event) implements AnnotatedTsdlPeriod {
  public AnnotatedTsdlPeriodImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, period, "Period referenced by AnnotatedPeriod must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, event, "Identifier of event referenced by AnnotatedPeriod must not be null.");
  }
}
