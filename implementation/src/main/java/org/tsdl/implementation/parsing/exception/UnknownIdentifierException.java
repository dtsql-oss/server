package org.tsdl.implementation.parsing.exception;

public class UnknownIdentifierException extends TsdlParseException {
  private static final String MESSAGE_TEMPLATE = "Identifier '%s' was referenced as %s, but never declared.";

  public UnknownIdentifierException(String identifierName, String purpose) {
    super(formatMessage(identifierName, purpose));
  }

  public UnknownIdentifierException(String identifierName, String purpose, Throwable cause) {
    super(formatMessage(identifierName, purpose), cause);
  }

  public UnknownIdentifierException(String identifierName, String purpose, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(formatMessage(identifierName, purpose), cause, enableSuppression, writableStackTrace);
  }

  private static String formatMessage(String identifierName, String purpose) {
    return MESSAGE_TEMPLATE.formatted(identifierName, purpose);
  }
}
