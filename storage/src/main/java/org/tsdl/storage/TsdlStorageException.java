package org.tsdl.storage;

/**
 * Indicates an error that occurred during accessing a storage solution (read, write or transformation).
 */
public class TsdlStorageException extends RuntimeException {
  public TsdlStorageException() {
  }

  public TsdlStorageException(String message) {
    super(message);
  }

  public TsdlStorageException(String message, Throwable cause) {
    super(message, cause);
  }

  public TsdlStorageException(Throwable cause) {
    super(cause);
  }

  public TsdlStorageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
