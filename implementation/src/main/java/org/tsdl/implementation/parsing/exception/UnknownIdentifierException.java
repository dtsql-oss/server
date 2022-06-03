package org.tsdl.implementation.parsing.exception;

public class UnknownIdentifierException extends TsdlParserException {
    private static final String MESSAGE_TEMPLATE = "Identifier '%s' was referenced, but never declared.";

    public UnknownIdentifierException(String identifierName) {
        super(formatMessage(identifierName));
    }

    public UnknownIdentifierException(String identifierName, Throwable cause) {
        super(formatMessage(identifierName), cause);
    }

    public UnknownIdentifierException(String identifierName, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(formatMessage(identifierName), cause, enableSuppression, writableStackTrace);
    }

    private static String formatMessage(String identifierName) {
        return MESSAGE_TEMPLATE.formatted(identifierName);
    }
}
