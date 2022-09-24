package org.tsdl.implementation.parsing.enums;

import org.tsdl.implementation.model.common.Identifiable;
import org.tsdl.implementation.model.filter.deviation.DeviationFilter;

/**
 * Represents possible types of {@link DeviationFilter}s.
 */
public enum DeviationFilterType implements Identifiable {
  AROUND_RELATIVE("around_rel"), AROUND_ABSOLUTE("around_abs");

  private final String representation;

  DeviationFilterType(String representation) {
    this.representation = representation;
  }

  public String representation() {
    return representation;
  }
}
