package com.http.connection.manager;

import com.http.connection.exception.ServiceErrorCode;
import com.http.connection.exception.ServiceException;
import com.http.connection.model.JSONHttpRequest;
import com.http.connection.model.JSONHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author : komal.nagar
 */
public class HttpRequestExecutor {
    private static final Logger log = LoggerFactory.getLogger(HttpRequestExecutor.class);
    private static HttpRequestExecutor ourInstance = new HttpRequestExecutor();

    public static HttpRequestExecutor getInstance() {
        return ourInstance;
    }

    private HttpRequestExecutor() {
    }

    public JSONHttpResponse execute(String clientName, JSONHttpRequest jsonHttpRequest) throws ServiceException {
        try {
            return new HttpConnectionManager(clientName, jsonHttpRequest).execute();
        } catch (IOException e) {
            log.error("exception while executing request : {}", jsonHttpRequest, e);
            throw new ServiceException(ServiceErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}