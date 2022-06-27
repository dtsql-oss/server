package org.tsdl.infrastructure.model.impl;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Instant;
import java.util.Locale;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link DataPoint}.
 *
 * @param timestamp date-time component of the instance
 * @param value value component of the instance
 */
public record TsdlDataPoint(Instant timestamp, Double value) implements DataPoint {
  private static final DecimalFormat VALUE_FORMATTER;

  static {
    // Double has a limited precision of 53 bits as per IEEE754, which amounts to roughly 16 decimal digits. Therefore, 16 significant decimal places
    // after a mandatory one should be enough (https://en.wikipedia.org/wiki/Floating-point_arithmetic#IEEE_754:_floating_point_in_modern_computers).
    VALUE_FORMATTER = new DecimalFormat("0.0################");
    var symbols = new DecimalFormatSymbols(Locale.US);
    symbols.setDecimalSeparator('.');
    VALUE_FORMATTER.setDecimalFormatSymbols(symbols);
    VALUE_FORMATTER.setGroupingUsed(false);
  }

  public TsdlDataPoint {
    Conditions.checkNotNull(Condition.ARGUMENT, timestamp, "Timestamp must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, value, "Value must not be null.");
  }

  public Long asInteger() {
    return Long.valueOf(value.toString());
  }

  public Double asDecimal() {
    return Double.valueOf(value.toString());
  }

  public String asText() {
    return VALUE_FORMATTER.format(value);
  }
}
