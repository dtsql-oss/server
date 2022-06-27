package org.tsdl.implementation.evaluation.impl.sample;

import java.util.Optional;
import org.tsdl.implementation.evaluation.impl.common.DefaultFormattable;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.common.TsdlOutputFormatter;
import org.tsdl.implementation.model.sample.TsdlSample;
import org.tsdl.implementation.model.sample.aggregation.TsdlAggregator;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

/**
 * Default implementation of {@link TsdlSample}.
 */
public record TsdlSampleImpl(
    TsdlAggregator aggregator,
    TsdlIdentifier identifier,
    Optional<TsdlOutputFormatter<TsdlSample>> formatter
) implements TsdlSample, DefaultFormattable<TsdlSample> {
  public TsdlSampleImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, aggregator, "Aggregator must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, identifier, "Identifier must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, formatter, "Formatter must not be null.");
  }
}
