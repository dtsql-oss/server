package org.tsdl.client.api.builder;

import java.util.Optional;

/**
 * Represents operands to a "SELECT PERIODS" component.
 */
public interface SelectOperand {
  Optional<Range> tolerance();

  /**
   * Represents an operand that references an event.
   */
  interface EventSelectOperand extends SelectOperand {
    String eventIdentifier();
  }
}
