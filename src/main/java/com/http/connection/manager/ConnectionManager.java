package com.http.connection.manager;

import com.http.connection.model.ConnectionConfig;
import com.http.connection.model.JSONHttpRequest;
import com.http.connection.model.JSONHttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Map;

@Slf4j
public abstract class ConnectionManager {
    private HttpClient httpClient;

    protected abstract ConnectionConfig getConnectionPool();

    protected ConnectionManager() {
        ConnectionConfig connectionConfig = getConnectionPool();

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(connectionConfig.getConnectTimeout())
                .setSocketTimeout(connectionConfig.getSocketTimeout())
                .setConnectionRequestTimeout(connectionConfig.getRequestTimeout())
                .build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(connectionConfig.getMaxTotalConnection());
        connectionManager.setDefaultMaxPerRoute(connectionConfig.getMaxConnectionPerRoute());

        httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connectionManager)
                .build();

        log.info("HttpServiceClient started for : {} with configs : {}", this.getClass().getSimpleName(), connectionConfig);
    }

    private HttpRequestBase createHttpRequest(JSONHttpRequest jsonHttpRequest) {
        byte[] data = jsonHttpRequest.getRawRequestBody();

        switch (jsonHttpRequest.getHttpMethod()) {
            case GET:
                HttpGet getRequest = new HttpGet(jsonHttpRequest.getUri());
                setRequestHeaders(getRequest, jsonHttpRequest.getHeaders());
                return getRequest;
            case POST:
                HttpPost postRequest = new HttpPost(jsonHttpRequest.getUri());
                if (data != null) {
                    postRequest.setEntity(new ByteArrayEntity(data));
                }
                setRequestHeaders(postRequest, jsonHttpRequest.getHeaders());
                return postRequest;
            case PUT:
                HttpPut putRequest = new HttpPut(jsonHttpRequest.getUri());
                if (data != null) {
                    putRequest.setEntity(new ByteArrayEntity(data));
                }
                setRequestHeaders(putRequest, jsonHttpRequest.getHeaders());
                return putRequest;
            case DELETE:
                HttpDelete deleteRequest = new HttpDelete(jsonHttpRequest.getUri());
                setRequestHeaders(deleteRequest, jsonHttpRequest.getHeaders());
                return deleteRequest;
            default:
                return null;
        }
    }

    private void setRequestHeaders(HttpRequestBase request, Map<String, String> headers) {
        if (headers == null) {
            return;
        }
        headers.forEach(request::addHeader);
    }

    protected JSONHttpResponse execute(JSONHttpRequest jsonHttpRequest) throws Exception {

        HttpRequestBase httpRequestBase = createHttpRequest(jsonHttpRequest);
        try {
            HttpResponse response = httpClient.execute(httpRequestBase);
            if (response == null || response.getStatusLine() == null) {
                log.warn("Got back null response for jsonHttpRequest : {}", httpRequestBase != null ? httpRequestBase.getRequestLine() : null);
                return new JSONHttpResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            }

            int httpStatus = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();

            if (entity == null) {
                log.warn("Got back null entity for jsonHttpRequest : {}", httpRequestBase != null ? httpRequestBase.getRequestLine() : null);
                return new JSONHttpResponse(httpStatus);
            }

            String rawBody = EntityUtils.toString(entity);

            return new JSONHttpResponse(httpStatus, rawBody);
        } catch (IOException e) {
            throw new Exception("Failure while making http call request:" + httpRequestBase + " requestId:" + (jsonHttpRequest.getHeaders() == null ? "-" : jsonHttpRequest.getHeaders().get("X-Request-Id")), e);
        }
    }
}
