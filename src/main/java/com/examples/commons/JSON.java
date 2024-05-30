package com.examples.commons;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

public final class JSON {
    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(PATTERN);

    private static final JsonMapper MAPPER = JsonMapper.builder()
            .enable(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .addModule(new Jdk8Module())
            .addModule(new JavaTimeModule()
                    .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DATE_FORMAT))
                    .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DATE_FORMAT)))
            .defaultDateFormat(new SimpleDateFormat(PATTERN))
            .defaultTimeZone(TimeZone.getDefault())
            .build();

    private JSON() {
    }

    public static ObjectNode createJSONObject() {
        return MAPPER.createObjectNode();
    }

    public static ArrayNode createJSONArray() {
        return MAPPER.createArrayNode();
    }

    public static String toJSONString(Object value) {
        if (Objects.isNull(value)) {
            return null;
        }

        try {
            return MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String toPrettyJSONString(Object value) {
        if (Objects.isNull(value)) {
            return null;
        }

        try {
            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static ObjectNode parseObject(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }

        try {
            return (ObjectNode) MAPPER.readTree(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <T> T parseObject(String value, Class<T> clazz) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }

        if (Objects.isNull(clazz)) {
            throw new IllegalArgumentException("Unrecognized Class: [null]");
        }

        try {
            return MAPPER.readValue(value, clazz);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <T> T parseObject(String value, TypeReference<T> typeRef) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }

        if (Objects.isNull(typeRef)) {
            throw new IllegalArgumentException("Unrecognized TypeReference: [null]");
        }

        try {
            return MAPPER.readValue(value, typeRef);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static ArrayNode parseArray(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }

        try {
            return (ArrayNode) MAPPER.readTree(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <T> List<T> parseArray(String value, Class<T> clazz) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }

        if (Objects.isNull(clazz)) {
            throw new IllegalArgumentException("Unrecognized Class: [null]");
        }

        try {
            CollectionType listType = MAPPER.getTypeFactory().constructCollectionType(ArrayList.class, clazz);
            return MAPPER.readValue(value, listType);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
