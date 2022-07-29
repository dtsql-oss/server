package org.tsdl.implementation.model.sample.aggregation;

import java.time.Instant;
import org.tsdl.implementation.model.sample.TsdlSample;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * An aggregation operator for a {@link TsdlSample} that only takes into account {@link DataPoint} instances that were recorded in a given period..
 */
public interface TsdlLocalAggregator extends TsdlAggregator {
  /**
   * The lower (left) bound of the period within which the aggregator operates. Must be before {@link TsdlLocalAggregator#upperBound()}.
   */
  Instant lowerBound();

  /**
   * The upper (right) bound of the period within which the aggregator operates. Must be after {@link TsdlLocalAggregator#lowerBound()}.
   */
  Instant upperBound();
}
