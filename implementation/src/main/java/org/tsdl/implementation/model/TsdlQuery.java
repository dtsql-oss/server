package org.tsdl.implementation.model;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.tsdl.implementation.model.choice.relation.TemporalOperator;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.connective.SinglePointFilterConnective;
import org.tsdl.implementation.model.event.TsdlEvent;
import org.tsdl.implementation.model.result.YieldStatement;
import org.tsdl.implementation.model.sample.TsdlSample;

/**
 * Representation of TSDl query.
 */
public interface TsdlQuery {
  Set<TsdlIdentifier> identifiers();

  Optional<SinglePointFilterConnective> filter();

  List<TsdlSample> samples();

  List<TsdlEvent> events();

  Optional<TemporalOperator> choice();

  YieldStatement result();
}
