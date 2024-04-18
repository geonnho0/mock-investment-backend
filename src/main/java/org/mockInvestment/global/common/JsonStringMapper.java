package org.mockInvestment.global.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

public class JsonStringMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();


    public static Optional<String> toJsonString(Object object) {
        try {
            return Optional.ofNullable(objectMapper.writeValueAsString(object));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

    public static <T> Optional<T> parseJsonString(String jsonString, Class<T> clazz) {
        try {
            return Optional.ofNullable(objectMapper.readValue(jsonString, clazz));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

}
