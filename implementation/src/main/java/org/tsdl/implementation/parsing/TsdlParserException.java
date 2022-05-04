package org.tsdl.implementation.parsing;

public class TsdlParserException extends RuntimeException {
    public TsdlParserException() {
    }

    public TsdlParserException(String message) {
        super(message);
    }

    public TsdlParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public TsdlParserException(Throwable cause) {
        super(cause);
    }

    public TsdlParserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
