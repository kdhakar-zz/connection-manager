package com.http.connection.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.http.connection.util.ConnectionPoolUtil;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.http.HttpStatus;

import java.io.IOException;

/**
 * @author : komal.nagar
 */
public class JSONHttpResponse {
    private int    statusCode;
    private byte[] rawResponseBody;

    public JSONHttpResponse(int statusCode) {
        this.statusCode = statusCode;
        this.rawResponseBody = new byte[]{};
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public <T> T getEntity(TypeReference<T> typeReference) throws IOException {
        return ConnectionPoolUtil.getObjectMapper().readValue(rawResponseBody, typeReference);
    }

    public byte[] getRawResponseBody() {
        return rawResponseBody;
    }

    public void setRawResponseBody(byte[] rawResponseBody) throws IOException {
        this.rawResponseBody = rawResponseBody;
    }

    public boolean isSuccess() {
        return statusCode == HttpStatus.SC_OK;
    }

    /*
    Returns if the client has thrown an error
     */
    public boolean isServerError() {
        return statusCode >= HttpStatus.SC_INTERNAL_SERVER_ERROR;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("statusCode", statusCode)
                .append("rawResponseBody", new String(rawResponseBody))
                .toString();
    }
}
