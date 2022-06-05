package org.tsdl.infrastructure.model.impl;

import java.time.Instant;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link DataPoint}.
 *
 * @param timestamp date-time component of the instance
 * @param value value component of the instance
 */
public record TsdlDataPoint(Instant timestamp, Object value) implements DataPoint {
  public TsdlDataPoint {
    Conditions.checkNotNull(Condition.ARGUMENT, timestamp, "Timestamp must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, value, "Value must not be null.");
  }

  @Override
  public Instant getTimestamp() {
    return timestamp();
  }

  @Override
  public Object getValue() {
    return value();
  }

  public Long asInteger() {
    return Long.valueOf(value.toString());
  }

  public Double asDecimal() {
    return Double.valueOf(value.toString());
  }

  public String asText() {
    return value.toString();
  }
}
