package com.http.connection;

import com.http.connection.exception.ServiceException;
import com.http.connection.manager.HttpConnectionManager;
import com.http.connection.manager.HttpRequestExecutor;
import com.http.connection.model.ConnectionConfig;
import com.http.connection.model.JSONHttpRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : komal.nagar
 */
public class Example {
    private static final String CLIENT1 = "client1";

    public static void main(String[] args) throws ServiceException {
        //create connection pool config for multiple clients.
        Map<String, ConnectionConfig> poolsConfigMap = new HashMap<>();
        ConnectionConfig config = new ConnectionConfig();
        config.setHost("localhost");
        config.setPort(8080);
        config.setConnectionPoolSize(100);
        config.setConnectionRequestTimeout(100);
        config.setConnectTimeout(100);
        config.setSocketTimeout(1000);
        poolsConfigMap.put(CLIENT1, config);

        HttpConnectionManager.init(poolsConfigMap);

        //now execute request for client1 (it will use its own connection pool).
        JSONHttpRequest jsonHttpRequest = new JSONHttpRequest();
        HttpRequestExecutor.getInstance().execute(CLIENT1, jsonHttpRequest);

        //release connection for set of clients.
        HttpConnectionManager.destroy(poolsConfigMap.keySet());
    }
}
