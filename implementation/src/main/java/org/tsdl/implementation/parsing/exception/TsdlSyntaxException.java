package org.tsdl.implementation.parsing.exception;

public class TsdlSyntaxException extends TsdlParseException {
  public TsdlSyntaxException() {
  }

  public TsdlSyntaxException(String message) {
    super(message);
  }

  public TsdlSyntaxException(String message, Throwable cause) {
    super(message, cause);
  }

  public TsdlSyntaxException(Throwable cause) {
    super(cause);
  }

  public TsdlSyntaxException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
