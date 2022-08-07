package org.tsdl.implementation.parsing.enums;

import org.tsdl.implementation.model.common.Identifiable;

/**
 * An aggregator.
 */
public enum AggregatorType implements Identifiable {
  AVERAGE("avg"), MAXIMUM("max"), MINIMUM("min"), SUM("sum"), COUNT("count"), INTEGRAL("integral"), STANDARD_DEVIATION("stddev"),
  TEMPORAL_AVERAGE("avg_t"), TEMPORAL_MAXIMUM("max_t"), TEMPORAL_MINIMUM("min_t"), TEMPORAL_SUM("sum_t"), TEMPORAL_COUNT("count_t"),
  TEMPORAL_STANDARD_DEVIATION("stddev_t");

  private final String representation;

  AggregatorType(String representation) {
    this.representation = representation;
  }

  public String representation() {
    return representation;
  }
}
