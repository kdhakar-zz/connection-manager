package com.http.connection.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author : komal.nagar
 */
public class MapperUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        //default object mapper.
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MAPPER.getFactory().configure(JsonFactory.Feature.INTERN_FIELD_NAMES, false);
    }

    public static ObjectMapper getMapper() {
        return MAPPER;
    }
}
