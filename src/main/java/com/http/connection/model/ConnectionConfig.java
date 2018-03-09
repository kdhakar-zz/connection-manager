package com.http.connection.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author : komal.nagar
 */
@Getter
@ToString
@AllArgsConstructor
public class ConnectionConfig {
    /*
     Timeout in milliseconds until a connection is established.
     */
    private int connectTimeout;
    /*
     Timeout in milliseconds which is the timeout for waiting for data.
     */
    private int socketTimeout;
    /*
     Timeout in milliseconds requesting a connection from the connection manager.
     */
    private int requestTimeout;

    /*
    Defines the overall connection limit for a connection pool.
     */
    private int maxTotalConnection;
    /*
     defines a connection limit per one HTTP route. In simple cases you can understand this as a per target host limit.
     Under the hood things are a bit more interesting: HttpClient maintains a couple of HttpRoute objects,
     which represent a chain of hosts each, like proxy1 -> proxy2 -> targetHost.
     Connections are pooled on per-route basis. In simple cases, when you're using default route-building mechanism
     and provide no proxy support, your routes are likely to include target host only, so per-route connection pool limit effectively
     becomes per-host limit.
     */
    private int maxConnectionPerRoute;
}
