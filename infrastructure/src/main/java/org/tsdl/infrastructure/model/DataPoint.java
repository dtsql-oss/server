package org.tsdl.infrastructure.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import org.tsdl.infrastructure.model.impl.TsdlDataPoint;

/**
 * Represents a data point, i.e. an item, in a time series.
 */
@JsonDeserialize(as = TsdlDataPoint.class)
public interface DataPoint {
  Instant timestamp();

  Double value();

  Long asInteger();

  String asText();

  static DataPoint of(Instant timestamp, Double value) {
    return new TsdlDataPoint(timestamp, value);
  }
}
