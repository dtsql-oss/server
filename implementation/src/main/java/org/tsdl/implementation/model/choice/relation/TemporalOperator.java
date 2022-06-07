package org.tsdl.implementation.model.choice.relation;

import java.util.List;
import org.tsdl.implementation.model.choice.AnnotatedTsdlPeriod;
import org.tsdl.infrastructure.model.TsdlPeriods;

/**
 * A temporal operator, relating events.
 */
public interface TemporalOperator {
  int cardinality();

  /**
   * Precondition: annotated periods are ordered by start time.
   */
  TsdlPeriods evaluate(List<AnnotatedTsdlPeriod> periods);
}
