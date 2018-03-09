# HTTP Connection pool

This module contains code of closeable HTTP Connection pool. 

- Sample usage:
    ````
    package com.http.connection;
    
    import com.fasterxml.jackson.core.type.TypeReference;
    import com.http.connection.manager.ConnectionManager;
    import com.http.connection.model.ConnectionConfig;
    import com.http.connection.model.JSONHttpRequest;
    import com.http.connection.model.JSONHttpResponse;
    import com.http.connection.util.MapperUtil;
    
    import java.util.HashMap;
    import java.util.Map;
    import java.util.UUID;
    
    /**
     * @author : komal.nagar
     */
    public class SampleClient extends ConnectionManager {
    
        public static void main(String[] args) throws Exception {
            new SampleClient().useConnectionManager();
        }
    
        private String useConnectionManager() throws Exception {
            JSONHttpRequest jsonHttpRequest = new JSONHttpRequest();
            jsonHttpRequest.setUri("/service/url-to-hit");
            jsonHttpRequest.setEntity(new Object()); //request body will go here.
            jsonHttpRequest.setHttpMethod(JSONHttpRequest.HttpMethod.POST);
            jsonHttpRequest.setHeaders(getHeaders());
            JSONHttpResponse jsonHttpResponse = execute(jsonHttpRequest);
            
            if (jsonHttpResponse.isSuccess()) {
                return MapperUtil.getMapper().readValue(jsonHttpResponse.getResponseBody(), new TypeReference<String>() {
                });
            } else {
                throw new Exception("Exception while executing http request");
            }
        }
    
        @Override
        protected ConnectionConfig getConnectionPool() {
            return new ConnectionConfig(1000, 5000, 1000, 1200, 1200);
        }
    
        private static Map<String, String> getHeaders() {
            Map<String, String> headers = new HashMap<>();
            headers.put("User-Agent", "your-agent");
            headers.put("Content-Type", "application/json");
            headers.put("X-Request-ID", UUID.randomUUID().toString());
    
            //and other headers.
    
            return headers;
        }
    }
