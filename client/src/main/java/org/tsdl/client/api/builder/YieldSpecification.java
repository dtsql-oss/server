package org.tsdl.client.api.builder;

import java.util.List;

/**
 * Represents the "YIELD" section of a TSDL query.
 */
public interface YieldSpecification {
  /**
   * TSDL query result type.
   */
  enum YieldType {
    DATA_POINTS, ALL_PERIODS, LONGEST_PERIOD, SHORTEST_PERIOD, SAMPLE, SAMPLES
  }

  String sample();

  List<String> samples();

  YieldType type();
}
