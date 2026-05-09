package com.progmeistars.pcbuilder.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.progmeistars.pcbuilder.dto.PartDTO;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Collections;
import java.util.Map;

@Converter
public class PartMapAttributeConverter implements AttributeConverter<Map<String, PartDTO>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, PartDTO> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "{}";
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Unable to serialize component map", e);
        }
    }

    @Override
    public Map<String, PartDTO> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(dbData, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Unable to deserialize component map", e);
        }
    }
}
