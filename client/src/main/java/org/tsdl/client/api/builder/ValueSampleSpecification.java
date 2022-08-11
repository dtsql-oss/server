package org.tsdl.client.api.builder;

import java.time.Instant;
import java.util.Optional;

/**
 * Represents a temporal sample in a TSDL query.
 */
public interface ValueSampleSpecification {
  /**
   * Value aggregator function.
   */
  enum ValueSampleType {
    AVERAGE, MAXIMUM, MINIMUM, SUM, COUNT, INTEGRAL, STANDARD_DEVIATION
  }

  String identifier();

  Optional<Instant> lowerBound();

  Optional<Instant> upperBound();

  Optional<EchoSpecification> echo();

  ValueSampleType type();
}
