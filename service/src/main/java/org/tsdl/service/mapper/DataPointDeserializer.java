package org.tsdl.service.mapper;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.time.Instant;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * A custom deserializer that allows mapping JSON representations to {@link DataPoint} instances.
 */
public class DataPointDeserializer extends StdDeserializer<DataPoint> {
  private static final ObjectMapper
      MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

  public DataPointDeserializer() {
    this(null);
  }

  protected DataPointDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public DataPoint deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JacksonException {
    var jsonObject = jp.getCodec().readTree(jp);
    var time = MAPPER.readValue(jsonObject.get("timestamp").toString(), Instant.class);
    var value = MAPPER.readValue(jsonObject.get("value").toString(), Double.class);
    return DataPoint.of(time, value);
  }
}
