package org.tsdl.implementation.model.choice.relation;

import java.util.List;
import org.tsdl.implementation.model.choice.AnnotatedTsdlPeriod;

/**
 * A binary temporal operator that represents the inverse relation to another binary temporal operator. More precisely, if two events (A, B) satisfy
 * a temporal relation (in this order of arguments), then the inverse relation is satisfied for arguments (B, A), in exactly this order.
 */
public interface InverseBinaryTemporalOperator extends BinaryTemporalOperator {
  BinaryTemporalOperatorConstructor baseOperator();

  @Override
  default List<AnnotatedTsdlPeriod> evaluate(List<AnnotatedTsdlPeriod> periods) {
    var baseOperatorInstance = baseOperator().instantiate(operand2(), operand1(), tolerance().orElse(null));
    return baseOperatorInstance.evaluate(periods);
  }
}
