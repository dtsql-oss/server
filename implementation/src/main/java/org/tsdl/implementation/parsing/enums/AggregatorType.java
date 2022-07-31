package org.tsdl.implementation.parsing.enums;

import org.tsdl.implementation.model.common.Identifiable;

/**
 * An aggregator.
 */
public enum AggregatorType implements Identifiable {
  AVERAGE("avg"), MAXIMUM("max"), MINIMUM("min"), SUM("sum"), COUNT("count"), INTEGRAL("integral");

  private final String representation;

  AggregatorType(String representation) {
    this.representation = representation;
  }

  public String representation() {
    return representation;
  }
}
