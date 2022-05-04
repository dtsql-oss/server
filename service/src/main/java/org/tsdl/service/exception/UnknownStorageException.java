package org.tsdl.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnknownStorageException extends Exception{
    public UnknownStorageException() {
        super();
    }

    public UnknownStorageException(String message) {
        super(message);
    }

    public UnknownStorageException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownStorageException(Throwable cause) {
        super(cause);
    }

    protected UnknownStorageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
