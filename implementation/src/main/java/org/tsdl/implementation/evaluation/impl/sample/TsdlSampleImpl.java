package org.tsdl.implementation.evaluation.impl.sample;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
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
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode
@ToString
public final class TsdlSampleImpl implements TsdlSample, DefaultFormattable<TsdlSample> {
  private final TsdlAggregator aggregator;
  private final TsdlIdentifier identifier;

  @Getter(AccessLevel.NONE)
  private final TsdlOutputFormatter<TsdlSample> formatterValue;

  /**
   * Initializes a {@link TsdlSampleImpl} instance.
   */
  public TsdlSampleImpl(TsdlAggregator aggregator, TsdlIdentifier identifier, TsdlOutputFormatter<TsdlSample> formatter) {
    Conditions.checkNotNull(Condition.ARGUMENT, aggregator, "Aggregator must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, identifier, "Identifier must not be null.");
    this.aggregator = aggregator;
    this.identifier = identifier;
    this.formatterValue = formatter;
  }

  @Override
  public Optional<TsdlOutputFormatter<TsdlSample>> formatter() {
    return Optional.ofNullable(formatterValue);
  }
}
