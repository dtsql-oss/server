package org.tsdl.implementation.evaluation.impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.tsdl.implementation.model.TsdlQuery;
import org.tsdl.implementation.model.choice.relation.TemporalOperator;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.connective.SinglePointFilterConnective;
import org.tsdl.implementation.model.event.TsdlEvent;
import org.tsdl.implementation.model.result.YieldStatement;
import org.tsdl.implementation.model.sample.TsdlSample;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

/**
 * Default implementation of {@link TsdlQuery}.
 */
@Builder
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode
@ToString
public final class TsdlQueryImpl implements TsdlQuery {
  @Singular
  private final Set<TsdlIdentifier> identifiers;
  private final SinglePointFilterConnective filterValue;
  @Singular
  private final List<TsdlSample> samples;
  @Singular
  private final List<TsdlEvent> events;
  private final TemporalOperator choiceValue;
  private final YieldStatement result;

  /**
   * Initializes a {@link TsdlQueryImpl} instance.
   */
  public TsdlQueryImpl(
      Set<TsdlIdentifier> identifiers,
      SinglePointFilterConnective filterValue,
      List<TsdlSample> samples,
      List<TsdlEvent> events,
      TemporalOperator choiceValue,
      YieldStatement result
  ) {
    Conditions.checkNotNull(Condition.ARGUMENT, identifiers, "Set of identifiers must no be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, samples, "List of samples must no be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, events, "List of events must no be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, result, "Result format must no be null.");
    this.identifiers = identifiers;
    this.filterValue = filterValue;
    this.samples = samples;
    this.events = events;
    this.choiceValue = choiceValue;
    this.result = result;
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
