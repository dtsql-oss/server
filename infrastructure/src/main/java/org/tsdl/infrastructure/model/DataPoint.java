package org.tsdl.infrastructure.model;

import java.time.Instant;
import org.tsdl.infrastructure.model.impl.TsdlDataPoint;

/**
 * Represents a data point, i.e. an item, in a time series.
 */
public interface DataPoint {
  Instant getTimestamp();

  Object getValue();

  Long asInteger();

  Double asDecimal();

  String asText();

  static DataPoint of(Instant timestamp, Object value) {
    return new TsdlDataPoint(timestamp, value);
  }
}
