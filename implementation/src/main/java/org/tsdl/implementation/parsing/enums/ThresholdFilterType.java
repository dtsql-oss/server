package org.tsdl.implementation.parsing.enums;

import org.tsdl.implementation.model.common.Identifiable;
import org.tsdl.implementation.model.filter.threshold.ThresholdFilter;

/**
 * Represents possible types of {@link ThresholdFilter}s.
 */
public enum ThresholdFilterType implements Identifiable {
  GT("gt"), LT("lt");

  private final String representation;

  ThresholdFilterType(String representation) {
    this.representation = representation;
  }

  public String representation() {
    return representation;
  }
}
