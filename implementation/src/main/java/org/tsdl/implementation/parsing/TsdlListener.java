package org.tsdl.implementation.parsing;


import org.tsdl.grammar.TsdlBaseListener;
import org.tsdl.grammar.TsdlParser;
import org.tsdl.implementation.model.TsdlOperator;
import org.tsdl.implementation.model.TsdlQuery;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.ParsePosition;

// TODO use visitor instead of listener?
public class TsdlListener extends TsdlBaseListener {
    private TsdlQuery query;
    private TsdlOperator operator;
    private Double threshold;

    @Override
    public void exitTsdl(TsdlParser.TsdlContext ctx) {
        if (query == null) {
            throw new TsdlParserException("Could not extract TSDL query.");
        }
    }

    @Override
    public void exitLine(TsdlParser.LineContext ctx) {
        if (operator != null && threshold != null) {
            query = new TsdlQuery(operator, threshold);
        }
    }

    @Override
    public void enterOperator(TsdlParser.OperatorContext ctx) {
        var operatorString = ctx.OPERATOR_ID().getText();
        operator = TsdlOperator.fromString(operatorString);
    }

    @Override
    public void enterThreshold(TsdlParser.ThresholdContext ctx) {
        try {
            threshold = parseNumber(ctx.threshold_value().getText());
        } catch (ParseException e) {
            throw new TsdlParserException("Parsed threshold has invalid number format", e);
        }
    }

    private Double parseNumber(String number) throws ParseException {
        var decimalFormat = new DecimalFormat();

        var symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator(',');
        decimalFormat.setDecimalFormatSymbols(symbols);

        var parsePosition = new ParsePosition(0);
        var parsedNumber = decimalFormat.parse(number, parsePosition);
        if (parsePosition.getIndex() != number.length()) {
            throw new ParseException("Failed to parse entire string: '%s'", parsePosition.getIndex());
        }
        return parsedNumber.doubleValue();
    }

    public TsdlQuery getQuery() {
        return query;
    }
}
