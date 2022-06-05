package org.tsdl.implementation.model;

import java.util.List;
import java.util.Set;
import org.tsdl.implementation.model.choice.relation.TemporalOperator;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.connective.SinglePointFilterConnective;
import org.tsdl.implementation.model.event.TsdlEvent;
import org.tsdl.implementation.model.result.ResultFormat;
import org.tsdl.implementation.model.sample.TsdlSample;

public interface TsdlQuery {
  Set<TsdlIdentifier> identifiers();

  SinglePointFilterConnective filter();

  List<TsdlSample> samples();

  List<TsdlEvent> events();

  TemporalOperator choice();

  ResultFormat result();
}
