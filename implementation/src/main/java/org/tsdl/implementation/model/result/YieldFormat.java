package org.tsdl.implementation.model.result;

import org.tsdl.implementation.model.common.Identifiable;

/**
 * The result format of a {@link org.tsdl.implementation.model.TsdlQuery}.
 */
public enum YieldFormat implements Identifiable {
  ALL_PERIODS("all periods"),
  LONGEST_PERIOD("longest period"),
  SHORTEST_PERIOD("shortest period"),
  DATA_POINTS("data points"),
  SAMPLE("sample");

  private final String representation;

  YieldFormat(String representation) {
    this.representation = representation;
  }

  public String representation() {
    return representation;
  }
}
