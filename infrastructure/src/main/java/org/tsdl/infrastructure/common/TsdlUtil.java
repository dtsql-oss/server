package org.tsdl.infrastructure.common;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Map;

/**
 * Bundles utility methods to be used across TSDL components.
 */
public final class TsdlUtil {
  private TsdlUtil() {
  }

  private static final Map<TsdlTimeUnit, Double> MILLIS_TO_UNIT_CONVERSION_FACTOR = Map.of(
      TsdlTimeUnit.MILLISECONDS, 1.0,
      TsdlTimeUnit.SECONDS, 1.0 / 1000,
      TsdlTimeUnit.MINUTES, 1.0 / (1000 * 60),
      TsdlTimeUnit.HOURS, 1.0 / (1000 * 60 * 60),
      TsdlTimeUnit.DAYS, 1.0 / (1000 * 60 * 60 * 24),
      TsdlTimeUnit.WEEKS, 1.0 / (1000 * 60 * 60 * 24 * 7)
  );

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

  public static double parseNumber(String str) throws ParseException {
    Conditions.checkNotNull(Condition.ARGUMENT, str, "String to parse as number must not be null.");
    return VALUE_FORMATTER.parse(str).doubleValue();
  }

  public static String formatNumber(Number num) {
    Conditions.checkNotNull(Condition.ARGUMENT, num, "Number to format as string must not be null.");
    return VALUE_FORMATTER.format(num);
  }

  /**
   * Determines whether a given {@code date} is within {@code intervalStart} and {@code intervalEnd} (both inclusively).
   */
  public static boolean isWithinRange(Instant date, Instant intervalStart, Instant intervalEnd) {
    Conditions.checkNotNull(Condition.ARGUMENT, date, "Date subject to range compatibility check must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, intervalStart, "Start date of interval for range compatibility check must not be null");
    Conditions.checkNotNull(Condition.ARGUMENT, intervalEnd, "End date of interval for range compatibility check must not be null");
    return !(date.isBefore(intervalStart) || date.isAfter(intervalEnd));
  }

  /**
   * Calculates the length of the timespan between the two timestamps {@code t1} and {@code t2} in the time unit {@code unit}.
   *
   * @param t1   first timestamp
   * @param t2   second timestamp
   * @param unit time unit the length of the timespan should be returned in
   * @return returns the time between {@code t1} and {@code t2} in {@code unit}. If {@code t1} is before {@code t2}, the return value is positive. If,
   *     on the other hand, {@code t2} is earlier than {@code t1}, then the return value is negative.
   */
  public static double getTimespan(Instant t1, Instant t2, TsdlTimeUnit unit) {
    var timespanMillis = ChronoUnit.MILLIS.between(t1, t2);

    Conditions.checkIsTrue(
        Condition.STATE,
        MILLIS_TO_UNIT_CONVERSION_FACTOR.containsKey(unit),
        "Cannot determine length of timespan in unit '%s'.",
        unit
    );

    return timespanMillis * MILLIS_TO_UNIT_CONVERSION_FACTOR.get(unit);
  }

  /**
   * Determines whether {@code value} represents a mathematical integer.
   */
  public static boolean isMathematicalInteger(double value) {
    return !Double.isNaN(value) && !Double.isInfinite(value) && value == Math.rint(value);
  }
}
