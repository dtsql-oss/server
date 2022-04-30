package org.tsdl.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ServiceResolutionException extends Exception{
    public ServiceResolutionException() {
    }

    public ServiceResolutionException(String message) {
        super(message);
    }

    public ServiceResolutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceResolutionException(Throwable cause) {
        super(cause);
    }

    public ServiceResolutionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
