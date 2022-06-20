package org.tsdl.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidRequestException extends ServiceResolutionException {
  public InvalidRequestException() {
    super();
  }

  public InvalidRequestException(String message) {
    super(message);
  }

  public InvalidRequestException(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidRequestException(Throwable cause) {
    super(cause);
  }

  protected InvalidRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
