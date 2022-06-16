package org.tsdl.implementation.parsing.exception;

public class InvalidReferenceException extends TsdlParserException {
  private static final String MESSAGE_TEMPLATE = "Identifier '%s' was used as '%s' reference, but never declared as such.";

  public InvalidReferenceException(String identifierName, String requiredPurpose) {
    super(formatMessage(identifierName, requiredPurpose));
  }

  public InvalidReferenceException(String identifierName, String requiredPurpose, Throwable cause) {
    super(formatMessage(identifierName, requiredPurpose), cause);
  }

  public InvalidReferenceException(String identifierName, String requiredPurpose, Throwable cause, boolean enableSuppression,
                                   boolean writableStackTrace) {
    super(formatMessage(identifierName, requiredPurpose), cause, enableSuppression, writableStackTrace);
  }

  private static String formatMessage(String identifierName, String requiredPurpose) {
    return MESSAGE_TEMPLATE.formatted(identifierName, requiredPurpose);
  }
}
