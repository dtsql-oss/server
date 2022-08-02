package org.tsdl.client.util;

import java.util.Map;
import java.util.TreeMap;

/**
 * Represents an exception that occurred during consumption of the TSDL API.
 */
public class TsdlClientServiceException extends TsdlClientException {
  private TreeMap<Integer, String> errorTrace;
  private String errorBody;

  public TsdlClientServiceException(String message, Map<Integer, String> errorTrace, String errorBody) {
    super(message);
    this.errorTrace = new TreeMap<>(errorTrace);
    this.errorBody = errorBody;
  }

  public TsdlClientServiceException(String message, Throwable cause) {
    super(message, cause);
  }

  public String errorBody() {
    return errorBody;
  }

  public TreeMap<Integer, String> errorTrace() {
    return errorTrace;
  }
}
