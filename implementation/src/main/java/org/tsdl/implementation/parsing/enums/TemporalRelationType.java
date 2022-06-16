package org.tsdl.implementation.parsing.enums;

import org.tsdl.implementation.model.common.Identifiable;

/**
 * A temporal relation.
 */
public enum TemporalRelationType implements Identifiable {
  PRECEDES("precedes"), FOLLOWS("follows");

  private final String representation;

  TemporalRelationType(String representation) {
    this.representation = representation;
  }

  @Override
  public String representation() {
    return representation;
  }
}
