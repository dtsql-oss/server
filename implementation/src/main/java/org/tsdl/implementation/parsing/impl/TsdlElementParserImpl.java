package org.tsdl.implementation.parsing.impl;

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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.NoSuchElementException;

public class TsdlElementParserImpl implements TsdlElementParser {
    @Override
    public ConnectiveIdentifier parseConnectiveIdentifier(String str) {
        Conditions.checkNotNull(Condition.ARGUMENT, str, "String to parse must not be null");
        return parseEnumMember(ConnectiveIdentifier.class, str);
    }

    @Override
    public FilterType parseFilterType(String str) {
        Conditions.checkNotNull(Condition.ARGUMENT, str, "String to parse must not be null");
        return parseEnumMember(FilterType.class, str);
    }

    @Override
    public ResultFormat parseResultFormat(String str) {
        Conditions.checkNotNull(Condition.ARGUMENT, str, "String to parse must not be null");
        return parseEnumMember(ResultFormat.class, str);
    }

    @Override
    public AggregatorType parseAggregatorType(String str) {
        Conditions.checkNotNull(Condition.ARGUMENT, str, "String to parse must not be null");
        return parseEnumMember(AggregatorType.class, str);
    }

    @Override
    public TemporalRelationType parseTemporalRelationType(String str) {
        Conditions.checkNotNull(Condition.ARGUMENT, str, "String to parse must not be null");
        return parseEnumMember(TemporalRelationType.class, str);
    }

    @Override
    public Double parseNumber(String str) {
        Conditions.checkNotNull(Condition.ARGUMENT, str, "String to parse must not be null");

        var decimalFormat = new DecimalFormat();

        var symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator(',');
        decimalFormat.setDecimalFormatSymbols(symbols);

        var parsePosition = new ParsePosition(0);
        var parsedNumber = decimalFormat.parse(str, parsePosition);
        if (parsePosition.getIndex() != str.length()) {
            throw new TsdlParserException("Parsing number failed.",
              new ParseException("Failed to parse entire string: '%s' at index %d.".formatted(str, parsePosition.getIndex()),
                parsePosition.getIndex()));
        }
        return parsedNumber.doubleValue();
    }

    public <T extends Identifiable> T parseEnumMember(Class<? extends T> clazz, String str) {
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
