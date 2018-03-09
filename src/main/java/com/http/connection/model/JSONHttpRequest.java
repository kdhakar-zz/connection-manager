package com.http.connection.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.http.connection.util.MapperUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class JSONHttpRequest {
    public enum HttpMethod {
        GET,
        PUT,
        POST,
        DELETE
    }

    private String uri;
    private HttpMethod httpMethod;
    private Map<String, String> headers;
    private byte[] rawRequestBody;

    public void setEntity(Object entity) throws JsonProcessingException {
        this.rawRequestBody = MapperUtil.getMapper().writeValueAsBytes(entity);
    }
}
