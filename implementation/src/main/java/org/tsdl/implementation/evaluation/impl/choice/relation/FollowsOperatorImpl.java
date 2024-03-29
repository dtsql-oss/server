package org.tsdl.implementation.evaluation.impl.choice.relation;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.tsdl.implementation.model.choice.relation.BinaryTemporalOperatorConstructor;
import org.tsdl.implementation.model.choice.relation.FollowsOperator;
import org.tsdl.implementation.model.choice.relation.TemporalOperand;
import org.tsdl.implementation.model.common.TsdlDuration;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

/**
 * Default implementation of {@link FollowsOperator}.
 */
@Slf4j
public record FollowsOperatorImpl(TemporalOperand operand1, TemporalOperand operand2, TsdlDuration toleranceValue) implements FollowsOperator {
  public FollowsOperatorImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, operand1, "First operand of 'follows' operator must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, operand2, "Second operand of 'follows' operator must not be null.");
  }

  @Override
  public BinaryTemporalOperatorConstructor baseOperator() {
    return PrecedesOperatorImpl::new;
  }

  @Override
  public String representation() {
    return "(%s-precedes-%s)".formatted(operand2.representation(), operand1.representation());
  }

  @Override
  public Optional<TsdlDuration> tolerance() {
    return Optional.ofNullable(toleranceValue);
  }
}
