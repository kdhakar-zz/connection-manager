package com.http.connection.manager;

import com.http.connection.exception.ServiceErrorCode;
import com.http.connection.exception.ServiceException;
import com.http.connection.model.ConnectionConfig;
import com.http.connection.model.JSONHttpRequest;
import com.http.connection.model.JSONHttpResponse;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author : komal.nagar
 */
public class HttpConnectionManager {
    private static final Logger logger = LoggerFactory.getLogger(HttpConnectionManager.class);
    private static final String UTF_8 = "UTF-8";

    private String urlString;
    private String client;
    private JSONHttpRequest jsonHttpRequest;

    private static Map<String, ConnectionConfig> poolConfigMap;
    private static Map<String, CloseableHttpClient> closeableHttpClientMap;

    /*
    Init method to initialize poolConfig and CloseableHttpClient with custom configs per client.
     */
    public static void init(Map<String, ConnectionConfig> poolsConfigMap) {
        poolConfigMap = poolsConfigMap;

        closeableHttpClientMap = new HashMap<>();
        for (Map.Entry<String, ConnectionConfig> entry : poolsConfigMap.entrySet()) {
            ConnectionConfig poolConfig = entry.getValue();

            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(poolConfig.getConnectTimeout())
                    .setConnectionRequestTimeout(poolConfig.getConnectionRequestTimeout())
                    .setSocketTimeout(poolConfig.getSocketTimeout())
                    .build();

            PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
            connectionManager.setMaxTotal(poolConfig.getConnectionPoolSize());
            connectionManager.setDefaultMaxPerRoute(poolConfig.getConnectionPoolSize());

            CloseableHttpClient closeableHttpClient = HttpClients.custom()
                    .setDefaultRequestConfig(requestConfig)
                    .setConnectionManager(connectionManager)
                    .build();

            closeableHttpClientMap.put(entry.getKey(), closeableHttpClient);
        }
        logger.info("HttpConnectionManager initialization done for services : {}", poolConfigMap.keySet());
    }

    /*
    Method to release connections.
     */
    public static void destroy(Set<String> clientList) throws ServiceException {
        for (String clientId : clientList) {
            if (closeableHttpClientMap.containsKey(clientId)) {
                try {
                    closeableHttpClientMap.get(clientId).close();
                } catch (IOException e) {
                    logger.error("Exception while releasing connections", e);
                    throw new ServiceException(ServiceErrorCode.INTERNAL_SERVER_ERROR);
                }
            }
        }
        logger.info("HttpConnectionManager destroyed successfully for clients : {}", poolConfigMap.keySet());
    }

    /*
    release connections for specific clientId.
     */
    public static void closeConnectionPool(String clientId) throws ServiceException {
        destroy(new HashSet<>(Collections.singletonList(clientId)));
    }

    /*
    Constructor to get instance and initialize object level variables.
     */
    HttpConnectionManager(String client, JSONHttpRequest jsonHttpRequest) throws ServiceException {
        this.jsonHttpRequest = jsonHttpRequest;
        this.client = client;

        try {
            ConnectionConfig commandPoolConfig = poolConfigMap.get(client);
            URI uri = new URIBuilder()
                    .setScheme("http")
                    .setHost(commandPoolConfig.getHost())
                    .setPort(commandPoolConfig.getPort())
                    .setPath(jsonHttpRequest.getUri())
                    .build();
            this.urlString = URLDecoder.decode(uri.toString(), UTF_8);
        } catch (URISyntaxException | UnsupportedEncodingException e) {
            logger.error("Exception while constructing url and setting object level variables for client : {} and jsonHttpRequest : {}", client, jsonHttpRequest);
            throw new ServiceException(ServiceErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /*
    Method to execute a request.
     */
    JSONHttpResponse execute() throws ServiceException, IOException {
        HttpRequestBase request = createHttpRequest(jsonHttpRequest);

        if (request == null) {
            logger.error("Invalid request for jsonHttpRequest : {}", jsonHttpRequest);
            return customResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }

        //try with resources (for response and connection), resource will get release automatically in finally block.
        try (CloseableHttpResponse response = closeableHttpClientMap.get(client).execute(request)) {
            if (response == null || response.getStatusLine() == null) {
                logger.warn("Got back null response for jsonHttpRequest : {}", request.getRequestLine());
                return customResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            }

            int httpStatus = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();

            if (entity == null) {
                logger.warn("Got back null entity for jsonHttpRequest : {}", request.getRequestLine());
                return customResponse(httpStatus);
            }

            byte[] rawBody = EntityUtils.toByteArray(entity);
            JSONHttpResponse jsonHttpResponse = new JSONHttpResponse(httpStatus);

            // log the response only if the server has thrown an error(5XX).
            if (jsonHttpResponse.isServerError()) {
                logger.error("Got back response: status : {} body : {}", httpStatus, new String(rawBody));
            }

            jsonHttpResponse.setRawResponseBody(rawBody);
            EntityUtils.consume(entity);
            return jsonHttpResponse;
        }
    }

    /*
    Method to return custom response with httpStatus code in case of error or empty response from upstream service.
     */
    private static JSONHttpResponse customResponse(int httpStatus) throws IOException {
        JSONHttpResponse jsonHttpResponse = new JSONHttpResponse(httpStatus);
        jsonHttpResponse.setRawResponseBody(new byte[0]);
        return jsonHttpResponse;
    }

    /*
    Method to create httpRequest based on jsonHttpRequest.
     */
    private HttpRequestBase createHttpRequest(JSONHttpRequest jsonHttpRequest) {
        byte[] data = jsonHttpRequest.getRawRequestBody();

        switch (jsonHttpRequest.getHttpMethod()) {
            case GET:
                HttpGet getRequest = new HttpGet(urlString);
                setRequestHeaders(getRequest, jsonHttpRequest.getHeaders());
                return getRequest;
            case POST:
                HttpPost postRequest = new HttpPost(urlString);
                if (data != null) {
                    postRequest.setEntity(new ByteArrayEntity(data));
                }
                setRequestHeaders(postRequest, jsonHttpRequest.getHeaders());
                return postRequest;
            case PUT:
                HttpPut putRequest = new HttpPut(urlString);
                if (data != null) {
                    putRequest.setEntity(new ByteArrayEntity(data));
                }
                setRequestHeaders(putRequest, jsonHttpRequest.getHeaders());
                return putRequest;
            case DELETE:
                HttpDelete deleteRequest = new HttpDelete(urlString);
                setRequestHeaders(deleteRequest, jsonHttpRequest.getHeaders());
                return deleteRequest;
            default:
                return null;
        }
    }

    /*
    Method to set headers in HttpRequestBase.
     */
    private static void setRequestHeaders(HttpRequestBase request, Map<String, String> headers) {
        if (headers == null) {
            return;
        }
        headers.forEach(request::addHeader);
    }
}
