package org.tsdl.client.api.builder;

import java.util.Optional;

/**
 * Represents the "SELECT" section of a TSDL query.
 */
public interface ChoiceSpecification extends ChoiceOperand {
  /**
   * Temporal relation.
   */
  enum ChoiceOperator {
    PRECEDES, FOLLOWS
  }

  ChoiceOperand operand1();

  ChoiceOperand operand2();

  Optional<Range> tolerance();

  ChoiceOperator type();
}
