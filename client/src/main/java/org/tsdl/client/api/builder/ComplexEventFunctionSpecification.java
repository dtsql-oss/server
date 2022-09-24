package org.tsdl.client.api.builder;

/**
 * Represents complex events that are not equivalent to filters.
 */
public interface ComplexEventFunctionSpecification extends EventFunctionSpecification {
  /**
   * Finds constant periods.
   */
  interface ConstantEventSpecification extends ComplexEventFunctionSpecification {
    String maximumSlope();

    String maximumRelativeDeviation();
  }

  /**
   * Finds monotonic (increase, decrease) periods.
   */
  interface MonotonicEventSpecification extends ComplexEventFunctionSpecification {
    /**
     * Determines the trend of monotonic period.
     */
    enum MonotonicEventType {
      INCREASE, DECREASE
    }

    String minimumChange();

    String maximumChange();

    String tolerance();

    MonotonicEventType type();
  }
}
