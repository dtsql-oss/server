package org.tsdl.implementation.model.sample.aggregation.value;

import java.time.Instant;
import java.util.Optional;
import org.tsdl.implementation.model.sample.aggregation.TsdlAggregator;

/**
 * An aggregation operator that operates over the value dimension of a time series.
 */
public interface ValueAggregator extends TsdlAggregator {
  /**
   * The lower (left) bound of the period within which the aggregator operates. Must be prior to {@link ValueAggregator#upperBound()}.
   */
  Optional<Instant> lowerBound();

  /**
   * The upper (right) bound of the period within which the aggregator operates. Must be after {@link ValueAggregator#lowerBound()}.
   */
  Optional<Instant> upperBound();
}
