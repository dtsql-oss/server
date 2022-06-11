package org.tsdl.implementation.model.sample.aggregation;

import java.util.List;
import org.tsdl.implementation.model.sample.TsdlSample;
import org.tsdl.implementation.parsing.enums.AggregatorType;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * An aggregation operator for a {@link TsdlSample}.
 */
public interface TsdlAggregator {
  /**
   * Computes the aggregator value if it has not been yet. If the value has already been computed, it is not computed again, but the previously
   * computed value is returned.
   */
  double compute(List<DataPoint> dataPoints);

  /**
   * Returns the computed aggregator value. If it has not been computed yet, a {@link IllegalStateException} is thrown.
   */
  double computedValue();

  /**
   * Indicates whether the aggregator value has already been computed.
   */
  boolean isComputed();

  AggregatorType type();
}
