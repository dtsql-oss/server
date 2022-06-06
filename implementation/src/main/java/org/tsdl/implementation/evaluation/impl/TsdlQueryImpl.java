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
  @Override
  public Optional<SinglePointFilterConnective> filter() {
    return Optional.ofNullable(this.filterValue);
  }

  @Override
  public Optional<TemporalOperator> choice() {
    return Optional.ofNullable(this.choiceValue);
  }
}
