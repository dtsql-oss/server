package org.tsdl.implementation.model.choice.relation;

/**
 * A temporal operator, relating events.
 */
public interface TemporalOperator {
  int cardinality();

  boolean isTrue();
}
