package org.tsdl.implementation.parsing.exception;

public class DuplicateIdentifierException extends TsdlParserException {
  private static final String MESSAGE_TEMPLATE = "Identifiers must be unique, but '%s' has been declared more than once.";

  public DuplicateIdentifierException(String identifierName) {
    super(formatMessage(identifierName));
  }

  public DuplicateIdentifierException(String identifierName, Throwable cause) {
    super(formatMessage(identifierName), cause);
  }

  public DuplicateIdentifierException(String identifierName, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(formatMessage(identifierName), cause, enableSuppression, writableStackTrace);
  }

  private static String formatMessage(String identifierName) {
    return String.format(MESSAGE_TEMPLATE, identifierName);
  }
}
