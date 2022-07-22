package org.tsdl.implementation.parsing.enums;

import org.tsdl.implementation.model.common.Identifiable;
import org.tsdl.implementation.model.filter.temporal.TemporalFilter;

/**
 * Represents possible types of {@link TemporalFilter}s.
 */
public enum TemporalFilterType implements Identifiable {
  AFTER("after"), BEFORE("before");

  private final String representation;

  TemporalFilterType(String representation) {
    this.representation = representation;
  }

  public String representation() {
    return representation;
  }
}
