package org.tsdl.implementation.model.sample;

import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.sample.aggregation.TsdlAggregator;

/**
 * A sample representing a special value in a TSDL query.
 */
public interface TsdlSample {
  TsdlAggregator aggregator();

  TsdlIdentifier identifier();
}
