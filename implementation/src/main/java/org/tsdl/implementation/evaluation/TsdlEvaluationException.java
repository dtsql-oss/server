package org.tsdl.implementation.evaluation;

public class TsdlEvaluationException extends Exception {
    public TsdlEvaluationException() {
    }

    public TsdlEvaluationException(String message) {
        super(message);
    }

    public TsdlEvaluationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TsdlEvaluationException(Throwable cause) {
        super(cause);
    }

    public TsdlEvaluationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
