package org.tsdl.implementation.parsing.impl;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.ParsePosition;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Locale;
import java.util.NoSuchElementException;
import org.tsdl.implementation.model.common.Identifiable;
import org.tsdl.implementation.model.result.YieldFormat;
import org.tsdl.implementation.parsing.TsdlElementParser;
import org.tsdl.implementation.parsing.enums.AggregatorType;
import org.tsdl.implementation.parsing.enums.ConnectiveIdentifier;
import org.tsdl.implementation.parsing.enums.TemporalFilterType;
import org.tsdl.implementation.parsing.enums.TemporalRelationType;
import org.tsdl.implementation.parsing.enums.ThresholdFilterType;
import org.tsdl.implementation.parsing.exception.TsdlParseException;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

/**
 * Default implementation of {@link TsdlElementParser}.
 */
public class TsdlElementParserImpl implements TsdlElementParser {
  public static final String STRING_TO_PARSE_MUST_NOT_BE_NULL = "String to parse must not be null";

  @Override
  public ConnectiveIdentifier parseConnectiveIdentifier(String str) {
    Conditions.checkNotNull(Condition.ARGUMENT, str, STRING_TO_PARSE_MUST_NOT_BE_NULL);
    return parseEnumMember(ConnectiveIdentifier.class, str);
  }

  @Override
  public ThresholdFilterType parseThresholdFilterType(String str) {
    Conditions.checkNotNull(Condition.ARGUMENT, str, STRING_TO_PARSE_MUST_NOT_BE_NULL);
    return parseEnumMember(ThresholdFilterType.class, str);
  }

  @Override
  public TemporalFilterType parseTemporalFilterType(String str) {
    Conditions.checkNotNull(Condition.ARGUMENT, str, STRING_TO_PARSE_MUST_NOT_BE_NULL);
    return parseEnumMember(TemporalFilterType.class, str);
  }

  @Override
  public YieldFormat parseResultFormat(String str) {
    Conditions.checkNotNull(Condition.ARGUMENT, str, STRING_TO_PARSE_MUST_NOT_BE_NULL);
    try {
      return parseEnumMember(YieldFormat.class, str);
    } catch (NoSuchElementException e) {
      if (str.startsWith(YieldFormat.SAMPLE.representation() + " ")) {
        return YieldFormat.SAMPLE;
      } else if (str.startsWith(YieldFormat.SAMPLE_SET.representation() + " ")) {
        return YieldFormat.SAMPLE_SET;
      }
      throw e;
    }
  }

  @Override
  public AggregatorType parseAggregatorType(String str) {
    Conditions.checkNotNull(Condition.ARGUMENT, str, STRING_TO_PARSE_MUST_NOT_BE_NULL);
    return parseEnumMember(AggregatorType.class, str);
  }

  @Override
  public TemporalRelationType parseTemporalRelationType(String str) {
    Conditions.checkNotNull(Condition.ARGUMENT, str, STRING_TO_PARSE_MUST_NOT_BE_NULL);
    return parseEnumMember(TemporalRelationType.class, str);
  }

  @Override
  public Double parseNumber(String str) {
    Conditions.checkNotNull(Condition.ARGUMENT, str, STRING_TO_PARSE_MUST_NOT_BE_NULL);

    var decimalFormat = new DecimalFormat();
    var symbols = new DecimalFormatSymbols(Locale.US);
    symbols.setDecimalSeparator('.');
    decimalFormat.setDecimalFormatSymbols(symbols);
    decimalFormat.setGroupingUsed(false);

    var parsePosition = new ParsePosition(0);
    var parsedNumber = decimalFormat.parse(str, parsePosition);
    if (parsePosition.getIndex() != str.length()) {
      throw new TsdlParseException("Parsing number failed.",
          new ParseException("Failed to parse entire string: '%s' at index %d.".formatted(str, parsePosition.getIndex()), parsePosition.getIndex()));
    }

    var num = parsedNumber.doubleValue();
    if (Double.isNaN(num)) {
      throw new TsdlParseException("NaN is not a valid number.");
    } else if (Double.isInfinite(num)) {
      throw new TsdlParseException("Infinity or negative infinity is not a valid number.");
    } else {
      return parsedNumber.doubleValue() + 0.0; // "+ 0.0" makes that the special case -0.0 is still returned as 0.0
    }
  }

  @Override
  public Integer parseInteger(String str) {
    var dbl = parseNumber(str);
    var isInteger = dbl == Math.floor(dbl); // double is not infinite by implementation of "parseDouble", so no !Double.isInfinite() check required
    if (isInteger) {
      return dbl.intValue();
    }

    throw new TsdlParseException("Expected double '%s' to be an integer.".formatted(dbl));
  }

  @Override
  public String parseStringLiteral(String str) {
    Conditions.checkNotNull(Condition.ARGUMENT, str, STRING_TO_PARSE_MUST_NOT_BE_NULL);

    if (str.length() < 2) {
      throw new TsdlParseException("String literals must contain at least two characters, but '%s' does not.".formatted(str));
    } else if (str.charAt(0) != '"' || str.charAt(str.length() - 1) != '"') {
      throw new TsdlParseException("String literals must begin and end with quote characters (\"), but '%s' does not.".formatted(str));
    }

    return str.substring(1, str.length() - 1);
  }

  @Override
  public Instant parseDateLiteral(String str) {
    Conditions.checkNotNull(Condition.ARGUMENT, str, STRING_TO_PARSE_MUST_NOT_BE_NULL);

    var stringValue = parseStringLiteral(str);
    try {
      return Instant.parse(stringValue);
    } catch (DateTimeParseException e) {
      throw new TsdlParseException("Invalid date '%s'".formatted(e.getParsedString()), e);
    }
  }

  private <T extends Identifiable> T parseEnumMember(Class<? extends T> clazz, String str) {
    return Arrays.stream(clazz.getEnumConstants())
        .filter(element -> element.representation().equals(str))
        .findFirst()
        .orElseThrow(() -> noSuchElementException(clazz, str));
  }

  private NoSuchElementException noSuchElementException(Class<?> type, String str) {
    return noSuchElementException(type.getSimpleName(), str);
  }

  private NoSuchElementException noSuchElementException(String type, String str) {
    return new NoSuchElementException("There is no '%s' member with representation '%s'.".formatted(type, str));
  }
}
