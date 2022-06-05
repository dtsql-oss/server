package org.tsdl.infrastructure.model;

import java.time.Instant;
import org.tsdl.infrastructure.model.impl.TsdlDataPoint;

public interface DataPoint {
  static DataPoint of(Instant timestamp, Object value) {
    return new TsdlDataPoint(timestamp, value);
  }

  Instant getTimestamp();

  Object getValue();

  Long asInteger();

  Double asDecimal();

  String asText();
}
