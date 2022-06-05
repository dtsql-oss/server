package org.tsdl.implementation.model.choice.relation;

import org.tsdl.implementation.model.event.TsdlEvent;

public interface BinaryTemporalOperator extends TemporalOperator {
  TsdlEvent operand1();

  TsdlEvent operand2();

  @Override
  default int cardinality() {
    return 2;
  }
}
