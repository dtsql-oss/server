package org.tsdl.implementation.model.choice.relation;

import java.util.List;
import org.tsdl.implementation.model.choice.AnnotatedTsdlPeriod;

/**
 * A temporal operator, relating events.
 */
public interface TemporalOperator extends TemporalOperand {
  int cardinality();

  /**
   * Precondition: annotated periods are ordered by start time.
   * For equal start times, the period whose declaring event has the lower index has precedence
   */
  List<AnnotatedTsdlPeriod> evaluate(List<AnnotatedTsdlPeriod> periods);
}
