package org.tsdl.implementation.evaluation.impl.sample;

import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.sample.TsdlSample;
import org.tsdl.implementation.model.sample.aggregation.TsdlAggregator;

/**
 * Default implementation of {@link TsdlSample}.
 */
public record TsdlSampleImpl(TsdlAggregator aggregator, TsdlIdentifier identifier) implements TsdlSample {
}
