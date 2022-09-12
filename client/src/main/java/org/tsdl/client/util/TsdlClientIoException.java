package org.tsdl.client.util;

/**
 * Represents an exception that occurred during a disk (I/O) operation..
 */
public class TsdlClientIoException extends TsdlClientException {
  public TsdlClientIoException(String message) {
    super(message);
  }

  public TsdlClientIoException(String message, Throwable cause) {
    super(message, cause);
  }
}
