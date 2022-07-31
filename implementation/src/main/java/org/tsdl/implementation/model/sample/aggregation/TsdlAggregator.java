package org.tsdl.implementation.model.sample.aggregation;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.tsdl.implementation.model.sample.TsdlSample;
import org.tsdl.implementation.parsing.enums.AggregatorType;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * An aggregation operator for a {@link TsdlSample}.
 */
public interface TsdlAggregator {
  /**
   * The lower (left) bound of the period within which the aggregator operates. Must be prior to {@link TsdlAggregator#upperBound()}.
   */
  Optional<Instant> lowerBound();

  /**
   * The upper (right) bound of the period within which the aggregator operates. Must be after {@link TsdlAggregator#lowerBound()}.
   */
  Optional<Instant> upperBound();

  /**
   * Computes the aggregator value if it has not been yet. If the value has already been computed, it is not computed again, but the previously
   * computed value is returned.
   */
  double compute(String sampleIdentifier, List<DataPoint> dataPoints);

  /**
   * Returns the computed aggregator value. If it has not been computed yet, a {@link IllegalStateException} is thrown.
   */
  double value();

  /**
   * Indicates whether the aggregator value has already been computed.
   */
  boolean isComputed();

  AggregatorType type();
}
