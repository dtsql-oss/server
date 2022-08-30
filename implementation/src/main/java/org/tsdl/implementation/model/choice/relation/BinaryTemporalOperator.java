package org.tsdl.implementation.model.choice.relation;

import java.util.Optional;
import org.tsdl.implementation.model.common.TsdlDuration;

/**
 * A binary temporal operator, relating two events.
 */
public interface BinaryTemporalOperator extends TemporalOperator {
  TemporalOperand operand1();

  TemporalOperand operand2();

  Optional<TsdlDuration> tolerance();

  @Override
  default int cardinality() {
    return 2;
  }
}
