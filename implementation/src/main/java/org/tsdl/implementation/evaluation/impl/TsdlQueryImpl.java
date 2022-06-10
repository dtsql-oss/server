package org.tsdl.implementation.evaluation.impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.Builder;
import lombok.Singular;
import org.tsdl.implementation.model.TsdlQuery;
import org.tsdl.implementation.model.choice.relation.TemporalOperator;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.connective.SinglePointFilterConnective;
import org.tsdl.implementation.model.event.TsdlEvent;
import org.tsdl.implementation.model.result.ResultFormat;
import org.tsdl.implementation.model.sample.TsdlSample;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

/**
 * Default implementation of {@link TsdlQuery}.
 */
@Builder
public record TsdlQueryImpl(
    @Singular Set<TsdlIdentifier> identifiers,
    SinglePointFilterConnective filterValue,
    @Singular List<TsdlSample> samples,
    @Singular List<TsdlEvent> events,
    TemporalOperator choiceValue,
    ResultFormat result
) implements TsdlQuery {
  public TsdlQueryImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, identifiers, "Set of identifiers must no be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, samples, "List of samples must no be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, events, "List of events must no be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, result, "Result format must no be null.");
  }

  @Override
  public Optional<SinglePointFilterConnective> filter() {
    return Optional.ofNullable(this.filterValue);
  }

  @Override
  public Optional<TemporalOperator> choice() {
    return Optional.ofNullable(this.choiceValue);
  }
}
