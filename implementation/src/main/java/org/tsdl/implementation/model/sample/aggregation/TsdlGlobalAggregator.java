package org.tsdl.implementation.model.sample.aggregation;

import org.tsdl.implementation.model.sample.TsdlSample;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * An aggregation operator for a {@link TsdlSample} that takes into account all {@link DataPoint} instances.
 */
public interface TsdlGlobalAggregator extends TsdlAggregator {
}
