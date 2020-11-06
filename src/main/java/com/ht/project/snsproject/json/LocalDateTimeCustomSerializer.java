package com.ht.project.snsproject.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;

public class LocalDateTimeCustomSerializer extends JsonSerializer<LocalDateTime> {
  @Override
  public void serialize(LocalDateTime localDateTime,
                        JsonGenerator jsonGenerator,
                        SerializerProvider serializerProvider) throws IOException {

    jsonGenerator.writeString(localDateTime.toString());
  }
}
