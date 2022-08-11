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

  double value();

  long asInteger();

  String asText();

  static DataPoint of(Instant timestamp, double value) {
    return new TsdlDataPoint(timestamp, value);
  }
}
