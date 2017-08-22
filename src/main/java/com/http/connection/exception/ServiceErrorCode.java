package com.http.connection.exception;

import org.apache.http.HttpStatus;

/**
 * @author : komal.nagar
 */
public enum ServiceErrorCode {
    INTERNAL_SERVER_ERROR("Something went wrong. Please try again later", HttpStatus.SC_INTERNAL_SERVER_ERROR);

    private String message;
    private int httpStatusCode;

    ServiceErrorCode(String message, int httpStatusCode) {
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }
}
