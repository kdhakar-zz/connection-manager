package com.http.connection.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.http.connection.util.ConnectionPoolUtil;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Map;

/**
 * @author : komal.nagar
 */
public class JSONHttpRequest {
    public enum HttpMethod {
        GET,
        PUT,
        POST,
        DELETE
    }

    private String                     uri;
    private HttpMethod httpMethod;
    private Map<String, String>        headers;
    private Object                     entity;
    private byte[]                     rawRequestBody;

    public JSONHttpRequest() {
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Object getEntity() {
        return entity;
    }

    public void setEntity(Object entity) throws JsonProcessingException {
        this.entity = entity;
        this.rawRequestBody = ConnectionPoolUtil.getObjectMapper().writeValueAsBytes(entity);
    }

    public byte[] getRawRequestBody() {
        return rawRequestBody;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("uri", uri)
                .append("httpMethod", httpMethod)
                .append("headers", headers)
                .append("entity", entity)
                .append("rawRequestBody", rawRequestBody)
                .toString();
    }
}
