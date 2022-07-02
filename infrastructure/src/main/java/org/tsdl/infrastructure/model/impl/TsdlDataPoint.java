package org.tsdl.infrastructure.model.impl;

import java.time.Instant;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.common.TsdlUtil;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link DataPoint}.
 */
@Jacksonized
@Builder
@Value
@Accessors(fluent = true)
public class TsdlDataPoint implements DataPoint {
  Instant timestamp;
  Double value;

  public TsdlDataPoint(Instant timestamp, Double value) {
    Conditions.checkNotNull(Condition.ARGUMENT, timestamp, "Timestamp must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, value, "Value must not be null.");
    this.timestamp = timestamp;
    this.value = value;
  }

  public Long asInteger() {
    return Long.valueOf(value.toString());
  }

  public String asText() {
    return TsdlUtil.formatNumber(value);
  }
}
