package org.tsdl.implementation.parsing.impl;

import org.tsdl.implementation.parsing.TsdlElementParser;
import org.tsdl.implementation.parsing.TsdlParserException;
import org.tsdl.implementation.parsing.enums.ConnectiveIdentifier;
import org.tsdl.implementation.parsing.enums.FilterType;
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
        return Arrays.stream(ConnectiveIdentifier.values())
          .filter(element -> element.representation().equals(str))
          .findFirst()
          .orElseThrow(() -> new NoSuchElementException("There is no 'ConnectiveIdentifier' member with representation '%s'.".formatted(str)));
    }

    @Override
    public FilterType parseFilterType(String str) {
        Conditions.checkNotNull(Condition.ARGUMENT, str, "String to parse must not be null");
        return Arrays.stream(FilterType.values())
          .filter(element -> element.representation().equals(str))
          .findFirst()
          .orElseThrow(() -> new NoSuchElementException("There is no 'ConnectiveIdentifier' member with representation '%s'.".formatted(str)));

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
}
