package org.tsdl.implementation.model.choice.relation;

import org.tsdl.implementation.model.event.TsdlEvent;

public interface UnaryTemporalOperator extends TemporalOperator {
    TsdlEvent operand1();

    @Override
    default int cardinality() {
        return 0;
    }
}
