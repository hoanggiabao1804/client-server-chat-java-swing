package util;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class ObjectMapperFactory {
    public static ObjectMapper create() {
        ObjectMapper objectMapper = new ObjectMapper();

        SimpleModule module = new SimpleModule();

        // Serializer
        module.addSerializer(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
            @Override
            public void serialize(
                    LocalDateTime value, JsonGenerator gen,
                    SerializerProvider serializers) throws IOException {
                gen.writeString(value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }
        });

        // Deserializer
        module.addDeserializer(
                LocalDateTime.class,
                new JsonDeserializer<LocalDateTime>() {
                    @Override
                    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                        return LocalDateTime.parse(p.getText(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    }
                });

        module.addSerializer(LocalDate.class,
                new JsonSerializer<LocalDate>() {
                    @Override
                    public void serialize(LocalDate value,
                            JsonGenerator gen,
                            SerializerProvider serializers) throws IOException {
                        gen.writeString(value.format(DateTimeFormatter.ISO_LOCAL_DATE));
                    }
                });

        module.addDeserializer(LocalDate.class,
                new JsonDeserializer<LocalDate>() {
                    @Override
                    public LocalDate deserialize(
                            JsonParser p,
                            DeserializationContext ctxt)
                            throws IOException {
                        return LocalDate.parse(p.getText(), DateTimeFormatter.ISO_LOCAL_DATE);
                    }
                });

        objectMapper.registerModule(module);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        return objectMapper;
    }
}
