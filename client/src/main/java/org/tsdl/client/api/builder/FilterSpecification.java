package org.tsdl.client.api.builder;

import java.time.Instant;

/**
 * Represents a filter atom in a TSDL query.
 */
public interface FilterSpecification {
  boolean isNegated();

  FilterSpecification negate();

  static FilterSpecification not(FilterSpecification filter) {
    return filter.negate();
  }

  /**
   * A threshold filter.
   */
  interface ThresholdFilterSpecification extends FilterSpecification {
    /**
     * Threshold filter function.
     */
    enum ThresholdFilterType {
      LESS_THAN, GREATER_THAN
    }

    String threshold();

    ThresholdFilterType type();
  }

  /**
   * A temporal filter.
   */
  interface TemporalFilterSpecification extends FilterSpecification {
    /**
     * Temporal filter function.
     */
    enum TemporalFilterType {
      BEFORE, AFTER
    }

    Instant argument();

    TemporalFilterType type();
  }

  /**
   * A deviation filter.
   */
  interface DeviationFilterSpecification extends FilterSpecification {
    /**
     * Deviation calculation strategy.
     */
    enum DeviationFilterType {
      RELATIVE, ABSOLUTE
    }

    String reference();

    String maximumDeviation();

    DeviationFilterType type();
  }
}
