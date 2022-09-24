package org.tsdl.client.impl.builder;

import java.util.Optional;
import org.tsdl.client.api.builder.Range;
import org.tsdl.client.api.builder.SelectOperand;

/**
 * Default implementation of {@link SelectOperand.EventSelectOperand}.
 */
public class EventSelectOperandImpl implements SelectOperand.EventSelectOperand {
  private final String eventIdentifier;
  private final Range tolerance;

  private EventSelectOperandImpl(String eventIdentifier, Range tolerance) {
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

  public static SelectOperand eventOperand(String eventIdentifier, Range tolerance) {
    return new EventSelectOperandImpl(eventIdentifier, tolerance);
  }

  public static SelectOperand eventOperand(String eventIdentifier) {
    return eventOperand(eventIdentifier, null);
  }
}
