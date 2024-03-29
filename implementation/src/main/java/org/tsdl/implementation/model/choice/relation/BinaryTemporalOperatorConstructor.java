package org.tsdl.implementation.model.choice.relation;

import java.util.function.BiFunction;
import org.tsdl.implementation.model.common.TsdlDuration;

/**
 * Represents a constructor of a {@link BinaryTemporalOperator} instance. Since the {@link BinaryTemporalOperator} interface dictates exactly two
 * methods, {@link BinaryTemporalOperator#operand1()} and {@link BinaryTemporalOperator#operand2()} (note that {@link TemporalOperator#cardinality()}
 * already has a default implementation of 2), the {@link BinaryTemporalOperatorConstructor} functional interface is simply a specialization of
 * {@link BiFunction} with type parameters {@link TemporalOperand}, {@link TemporalOperand} and {@link BinaryTemporalOperator}. The functional method
 * {@link #instantiate(TemporalOperand, TemporalOperand, TsdlDuration)} represents the constructor invocation of a {@link BinaryTemporalOperator}
 * implementation.
 */
@FunctionalInterface
public interface BinaryTemporalOperatorConstructor {
  BinaryTemporalOperator instantiate(TemporalOperand op1, TemporalOperand op2, TsdlDuration timeTolerance);
}
