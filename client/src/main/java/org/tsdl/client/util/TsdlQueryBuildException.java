package org.tsdl.client.util;

import org.tsdl.client.api.builder.TsdlQueryBuilder;

/**
 * Represents an exception that occurred during building a query with a {@link TsdlQueryBuilder} instance.
 */
public class TsdlQueryBuildException extends TsdlClientException {
  public TsdlQueryBuildException() {
  }

  public TsdlQueryBuildException(String message) {
    super(message);
  }

  public TsdlQueryBuildException(String message, Throwable cause) {
    super(message, cause);
  }

  public TsdlQueryBuildException(Throwable cause) {
    super(cause);
  }

  public TsdlQueryBuildException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
