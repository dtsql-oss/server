package org.tsdl.client.api.builder;

import java.util.Optional;

/**
 * Represents operands to a "SELECT" component.
 */
public interface ChoiceOperand {
  Optional<Range> tolerance();

  /**
   * Represents an operand that references an event.
   */
  interface EventChoiceOperand extends ChoiceOperand {
    String eventIdentifier();
  }
}
