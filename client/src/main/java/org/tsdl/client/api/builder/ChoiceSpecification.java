package org.tsdl.client.api.builder;

import java.util.Optional;

/**
 * Represents the "CHOOSE" section of a TSDL query.
 */
public interface ChoiceSpecification {
  /**
   * Temporal relation.
   */
  enum ChoiceOperator {
    PRECEDES, FOLLOWS
  }

  String operand1();

  String operand2();

  Optional<Range> tolerance();

  ChoiceOperator type();
}
