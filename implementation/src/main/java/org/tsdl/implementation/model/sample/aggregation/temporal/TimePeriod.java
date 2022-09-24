package org.tsdl.implementation.model.sample.aggregation.temporal;

import java.time.Instant;
import org.tsdl.infrastructure.common.TsdlTimeUnit;

/**
 * Simple representation of a time period, defined by start and end.
 */
public interface TimePeriod {
  Instant start();

  Instant end();

  double duration(TsdlTimeUnit unit);
}
