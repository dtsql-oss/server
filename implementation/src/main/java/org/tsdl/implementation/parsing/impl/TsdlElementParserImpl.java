package org.tsdl.implementation.parsing.impl;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.Locale;
import java.util.NoSuchElementException;
import org.tsdl.implementation.model.common.Identifiable;
import org.tsdl.implementation.model.result.ResultFormat;
import org.tsdl.implementation.parsing.TsdlElementParser;
import org.tsdl.implementation.parsing.enums.AggregatorType;
import org.tsdl.implementation.parsing.enums.ConnectiveIdentifier;
import org.tsdl.implementation.parsing.enums.FilterType;
import org.tsdl.implementation.parsing.enums.TemporalRelationType;
import org.tsdl.implementation.parsing.exception.TsdlParserException;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

public class TsdlElementParserImpl implements TsdlElementParser {

  public static final String STRING_TO_PARSE_MUST_NOT_BE_NULL = "String to parse must not be null";

  @Override
  public ConnectiveIdentifier parseConnectiveIdentifier(String str) {
    Conditions.checkNotNull(Condition.ARGUMENT, str, STRING_TO_PARSE_MUST_NOT_BE_NULL);
    return parseEnumMember(ConnectiveIdentifier.class, str);
  }

  @Override
  public FilterType parseFilterType(String str) {
    Conditions.checkNotNull(Condition.ARGUMENT, str, STRING_TO_PARSE_MUST_NOT_BE_NULL);
    return parseEnumMember(FilterType.class, str);
  }

  @Override
  public ResultFormat parseResultFormat(String str) {
    Conditions.checkNotNull(Condition.ARGUMENT, str, STRING_TO_PARSE_MUST_NOT_BE_NULL);
    return parseEnumMember(ResultFormat.class, str);
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
      throw new TsdlParserException("Parsing number failed.",
          new ParseException("Failed to parse entire string: '%s' at index %d.".formatted(str, parsePosition.getIndex()),
              parsePosition.getIndex()));
    }

    var num = parsedNumber.doubleValue();
    if (Double.isNaN(num)) {
      throw new TsdlParserException("NaN is not a valid number.");
    } else if (Double.isInfinite(num)) {
      throw new TsdlParserException("Infinity or negative infinity is not a valid number.");
    } else {
      return parsedNumber.doubleValue() + 0.0; // "+ 0.0" makes that special case -0.0 is still returned as 0.0
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
