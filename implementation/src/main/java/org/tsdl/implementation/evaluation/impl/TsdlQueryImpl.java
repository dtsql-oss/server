package org.tsdl.implementation.evaluation.impl;

import lombok.Builder;
import org.tsdl.implementation.model.TsdlQuery;
import org.tsdl.implementation.model.choice.relation.TemporalOperator;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.connective.SinglePointFilterConnective;
import org.tsdl.implementation.model.event.TsdlEvent;
import org.tsdl.implementation.model.result.ResultFormat;
import org.tsdl.implementation.model.sample.TsdlSample;

import java.util.List;
import java.util.Set;

@Builder
public record TsdlQueryImpl(
  Set<TsdlIdentifier> identifiers,
  SinglePointFilterConnective filter,
  List<TsdlSample> samples,
  List<TsdlEvent> events,
  TemporalOperator choice,
  ResultFormat yield
) implements TsdlQuery {
}
