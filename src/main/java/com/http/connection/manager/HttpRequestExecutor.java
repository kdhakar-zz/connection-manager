package com.http.connection.manager;

import com.http.connection.exception.HttpConnectionException;
import com.http.connection.model.JSONHttpRequest;
import com.http.connection.model.JSONHttpResponse;

import java.io.IOException;

/**
 * @author : komal.nagar
 */
public class HttpRequestExecutor {
    private static HttpRequestExecutor ourInstance = new HttpRequestExecutor();

    public static HttpRequestExecutor getInstance() {
        return ourInstance;
    }

    private HttpRequestExecutor() {
    }

    public JSONHttpResponse execute(String clientName, JSONHttpRequest jsonHttpRequest) throws HttpConnectionException {
        try {
            return new HttpConnectionManager(clientName, jsonHttpRequest).execute();
        } catch (IOException e) {
            throw new HttpConnectionException(e);
        }
    }
}