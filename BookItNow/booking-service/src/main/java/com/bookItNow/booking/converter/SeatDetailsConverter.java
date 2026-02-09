package com.bookItNow.booking.converter;

import com.bookItNow.common.dto.SeatDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.List;

@Converter
public class SeatDetailsConverter implements AttributeConverter<List<SeatDetails>, String> {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<SeatDetails> seatDetails) {
        try{
            return objectMapper.writeValueAsString(seatDetails);
        }catch(JsonProcessingException ex){
            throw new RuntimeException("JSON writing error", ex);
        }
    }

    @Override
    public List<SeatDetails> convertToEntityAttribute(String s) {
        try{
            return objectMapper.readValue(s, new TypeReference<List<SeatDetails>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON reading error", e);
        }
    }
}
