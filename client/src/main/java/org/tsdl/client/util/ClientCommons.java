package org.tsdl.client.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.ZoneOffset;
import java.util.TimeZone;
import org.tsdl.infrastructure.common.DataPointDeserializer;
import org.tsdl.infrastructure.dto.QueryResultDto;
import org.tsdl.infrastructure.model.DataPoint;

public final class ClientCommons {
  private ClientCommons() {
  }

  public static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
      .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
      .disable(DeserializationFeature.ACCEPT_FLOAT_AS_INT, DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
      .addModules(
          new JavaTimeModule(),
          new SimpleModule("CUSTOM_DESERIALIZERS")
              .addDeserializer(QueryResultDto.class, new QueryResultDtoDeserializer())
              .addDeserializer(DataPoint.class, new DataPointDeserializer())
      )
      .defaultTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC))
      .build();
}
