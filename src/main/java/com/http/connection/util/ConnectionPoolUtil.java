package com.http.connection.util;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author : komal.nagar
 */
public class ConnectionPoolUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
