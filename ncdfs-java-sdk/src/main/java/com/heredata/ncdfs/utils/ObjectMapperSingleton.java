package com.heredata.ncdfs.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.heredata.ncdfs.exception.NCDFSClientException;

/**
 * Default Jackson Mappings
 *
 * @author Jeremy Unruh
 */
public class ObjectMapperSingleton {

    private static final ObjectMapperSingleton INSTANCE = new ObjectMapperSingleton();

    ObjectMapper mapper;
    ObjectMapper rootMapper;

    private ObjectMapperSingleton() {

        mapper = new ObjectMapper();

        mapper.setSerializationInclusion(Include.NON_NULL);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        rootMapper = new ObjectMapper();
        rootMapper.setSerializationInclusion(Include.NON_NULL);
        rootMapper.enable(SerializationFeature.INDENT_OUTPUT);
        rootMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        rootMapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);
        rootMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        rootMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }


    public static ObjectMapper getContext(Class<?> type) {
        return type.getAnnotation(JsonRootName.class) == null ? INSTANCE.mapper : INSTANCE.rootMapper;
    }

    public static String transJson(Class<?> classLoader, Object entity) {
        try {
            return ObjectMapperSingleton.getContext(classLoader).writer().writeValueAsString(entity);
        } catch (JsonProcessingException e) {
            throw new NCDFSClientException("Entity trans into JSON is error. The entity is " + entity);
        }
    }

}
