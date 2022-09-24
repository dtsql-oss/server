package org.tsdl.infrastructure.model.impl;

import java.time.Instant;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.common.TsdlUtil;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link DataPoint}.
 */
public record TsdlDataPoint(Instant timestamp, double value) implements DataPoint {
  public TsdlDataPoint {
    Conditions.checkNotNull(Condition.ARGUMENT, timestamp, "Timestamp must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, value, "Value must not be null.");
  }

  public long asInteger() {
    return (long) value;
  }

  public String asText() {
    return TsdlUtil.formatNumber(value);
  }
}
