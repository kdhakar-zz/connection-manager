package com.http.connection.exception;

/**
 * @author : komal.nagar
 */
public class ServiceException extends Exception {
    private int httpStatusCode;
    private String message;

    public ServiceException(ServiceErrorCode serviceErrorCode) {
        super();
        this.message = serviceErrorCode.getMessage();
        this.httpStatusCode = serviceErrorCode.getHttpStatusCode();
    }

    public ServiceException(String message, int httpStatusCode) {
        super();
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
