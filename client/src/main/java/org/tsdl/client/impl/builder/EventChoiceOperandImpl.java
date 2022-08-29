package org.tsdl.client.impl.builder;

import java.util.Optional;
import org.tsdl.client.api.builder.ChoiceOperand;
import org.tsdl.client.api.builder.Range;

public class EventChoiceOperandImpl implements ChoiceOperand.EventChoiceOperand {
  private final String eventIdentifier;
  private final Range tolerance;

  private EventChoiceOperandImpl(String eventIdentifier, Range tolerance) {
    this.eventIdentifier = eventIdentifier;
    this.tolerance = tolerance;
  }

  @Override
  public Optional<Range> tolerance() {
    return Optional.ofNullable(tolerance);
  }

  @Override
  public String eventIdentifier() {
    return eventIdentifier;
  }

  public static ChoiceOperand eventOperand(String eventIdentifier, Range tolerance) {
    return new EventChoiceOperandImpl(eventIdentifier, tolerance);
  }

  public static ChoiceOperand eventOperand(String eventIdentifier) {
    return eventOperand(eventIdentifier, null);
  }
}
