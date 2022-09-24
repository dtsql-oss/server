package org.tsdl.implementation.parsing.exception;

public class TsdlParseException extends RuntimeException {
  public TsdlParseException() {
  }

  public TsdlParseException(String message) {
    super(message);
  }

  public TsdlParseException(String message, Throwable cause) {
    super(message, cause);
  }

  public TsdlParseException(Throwable cause) {
    super(cause);
  }

  public TsdlParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
