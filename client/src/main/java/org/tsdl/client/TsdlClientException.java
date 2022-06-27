package org.tsdl.client;

/**
 * Represents an exception that occurred during a TSDL Client operation.
 */
public class TsdlClientException extends RuntimeException {
  public TsdlClientException() {
  }

  public TsdlClientException(String message) {
    super(message);
  }

  public TsdlClientException(String message, Throwable cause) {
    super(message, cause);
  }

  public TsdlClientException(Throwable cause) {
    super(cause);
  }

  public TsdlClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
