package org.tsdl.client.api.builder;

import java.util.List;
import java.util.Optional;
import org.tsdl.infrastructure.common.TsdlTimeUnit;

/**
 * Represents a temporal sample in a TSDL query.
 */
public interface TemporalSampleSpecification {
  /**
   * Temporal aggregator function.
   */
  enum TemporalSampleType {
    AVERAGE, MAXIMUM, MINIMUM, SUM, STANDARD_DEVIATION, COUNT
  }

  String identifier();

  TsdlTimeUnit unit();

  List<QueryPeriod> periods();

  Optional<EchoSpecification> echo();

  TemporalSampleType type();
}
