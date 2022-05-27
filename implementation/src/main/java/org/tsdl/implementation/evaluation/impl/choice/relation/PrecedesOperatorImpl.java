package org.tsdl.implementation.evaluation.impl.choice.relation;

import org.tsdl.implementation.model.choice.relation.PrecedesOperator;
import org.tsdl.implementation.model.event.TsdlEvent;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

public record PrecedesOperatorImpl(TsdlEvent operand1, TsdlEvent operand2) implements PrecedesOperator {
    @Override
    public boolean isTrue() {
        Conditions.checkNotNull(Condition.ARGUMENT, operand1, "First event argument must not be null.");
        Conditions.checkNotNull(Condition.ARGUMENT, operand2, "Second event argument must not be null.");
        throw new UnsupportedOperationException();
    }
}
