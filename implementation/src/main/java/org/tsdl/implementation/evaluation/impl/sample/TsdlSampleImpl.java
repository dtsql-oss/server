package org.tsdl.implementation.evaluation.impl.sample;

import java.util.Optional;
import org.tsdl.implementation.evaluation.impl.common.DefaultFormattable;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.common.TsdlOutputFormatter;
import org.tsdl.implementation.model.sample.TsdlSample;
import org.tsdl.implementation.model.sample.aggregation.TsdlAggregator;

/**
 * Default implementation of {@link TsdlSample}.
 */
public record TsdlSampleImpl(
    TsdlAggregator aggregator,
    TsdlIdentifier identifier,
    Optional<TsdlOutputFormatter<TsdlSample>> formatter
) implements TsdlSample, DefaultFormattable<TsdlSample> {
}
