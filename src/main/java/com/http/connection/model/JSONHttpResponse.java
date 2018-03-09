package com.http.connection.model;

import org.apache.commons.lang3.StringUtils;

public class JSONHttpResponse {
    private final int statusCode;
    private final String responseBody;

    public JSONHttpResponse(int statusCode) {
        this(statusCode, StringUtils.EMPTY);
    }

    public JSONHttpResponse(int statusCode, String responseBody) {
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }

    @Override
    public String toString() {
        return ("statusCode:" + statusCode) +
                " responseBody:" + responseBody;
    }

    public boolean isSuccess() {
        return (this.getStatusCode() >= 200 && this.getStatusCode() <= 299);
    }
}
