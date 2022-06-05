package org.tsdl.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InputInterpretationException extends Exception {
  public InputInterpretationException() {
    super();
  }

  public InputInterpretationException(String message) {
    super(message);
  }

  public InputInterpretationException(String message, Throwable cause) {
    super(message, cause);
  }

  public InputInterpretationException(Throwable cause) {
    super(cause);
  }

  protected InputInterpretationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
