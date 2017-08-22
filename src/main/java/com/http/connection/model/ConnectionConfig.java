package com.http.connection.model;


import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.NotNull;

/**
 * @author : komal.nagar
 */
public class ConnectionConfig {
    @NotNull
    private String host;

    @NotNull
    private int port;

    @NotNull
    private int connectionPoolSize;

    //Connection timeout is the timeout until a connection with the server is established.
    @NotNull
    private int connectTimeout;

    //ConnectionRequestTimeout used when requesting a connection from the connection manager.
    @NotNull
    private int connectionRequestTimeout;

    //is the timeout to receive data (socket timeout).
    @NotNull
    private int socketTimeout;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getConnectionPoolSize() {
        return connectionPoolSize;
    }

    public void setConnectionPoolSize(int connectionPoolSize) {
        this.connectionPoolSize = connectionPoolSize;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getConnectionRequestTimeout() {
        return connectionRequestTimeout;
    }

    public void setConnectionRequestTimeout(int connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("host", host)
                .append("port", port)
                .append("connectionPoolSize", connectionPoolSize)
                .append("connectTimeout", connectTimeout)
                .append("connectionRequestTimeout", connectionRequestTimeout)
                .append("socketTimeout", socketTimeout)
                .toString();
    }
}
