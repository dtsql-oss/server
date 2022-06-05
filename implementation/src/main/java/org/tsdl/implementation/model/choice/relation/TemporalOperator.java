package org.tsdl.implementation.model.choice.relation;

public interface TemporalOperator {
  int cardinality();

  boolean isTrue();
}
