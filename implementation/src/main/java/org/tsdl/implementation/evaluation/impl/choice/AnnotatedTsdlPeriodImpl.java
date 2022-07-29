package org.tsdl.implementation.evaluation.impl.choice;

import java.util.Optional;
import org.tsdl.implementation.model.choice.AnnotatedTsdlPeriod;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.infrastructure.model.TsdlPeriod;

/**
 * Default implementation of {@link AnnotatedTsdlPeriod}.
 */
public record AnnotatedTsdlPeriodImpl(TsdlPeriod period, TsdlIdentifier event, DataPoint priorDataPointValue, DataPoint subsequentDataPointValue)
    implements AnnotatedTsdlPeriod {
  public AnnotatedTsdlPeriodImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, period, "Period referenced by AnnotatedPeriod must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, event, "Identifier of event referenced by AnnotatedPeriod must not be null.");
  }

  @Override
  public Optional<DataPoint> priorDataPoint() {
    return Optional.ofNullable(priorDataPointValue);
  }

  @Override
  public Optional<DataPoint> subsequentDataPoint() {
    return Optional.ofNullable(subsequentDataPointValue);
  }
}
