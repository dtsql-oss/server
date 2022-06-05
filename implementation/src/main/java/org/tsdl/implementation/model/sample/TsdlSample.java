package org.tsdl.implementation.model.sample;

import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.sample.aggregation.TsdlAggregator;

public interface TsdlSample {
  TsdlAggregator aggregator();

  TsdlIdentifier identifier();
}
