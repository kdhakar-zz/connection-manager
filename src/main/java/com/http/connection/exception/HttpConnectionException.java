package com.http.connection.exception;

/**
 * @author : komal.nagar
 */
public class HttpConnectionException extends Exception {
    public HttpConnectionException() {
    }

    public HttpConnectionException(String message) {
        super(message);
    }

    public HttpConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpConnectionException(Throwable cause) {
        super(cause);
    }

    public HttpConnectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
