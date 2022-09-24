package org.tsdl.client.api.builder;

import java.util.Optional;

/**
 * Represents the "SELECT PERIODS" section of a TSDL query.
 */
public interface SelectSpecification extends SelectOperand {
  /**
   * Temporal relation.
   */
  enum SelectOperator {
    PRECEDES, FOLLOWS
  }

  SelectOperand operand1();

  SelectOperand operand2();

  Optional<Range> tolerance();

  SelectOperator type();
}
