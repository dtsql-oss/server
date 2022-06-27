package org.tsdl.client;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;
import java.io.IOException;
import org.tsdl.infrastructure.dto.QueryResultDto;
import org.tsdl.infrastructure.model.QueryResultType;
import org.tsdl.infrastructure.model.impl.MultipleScalarResultImpl;
import org.tsdl.infrastructure.model.impl.SingularScalarResultImpl;
import org.tsdl.infrastructure.model.impl.TsdlDataPointsImpl;
import org.tsdl.infrastructure.model.impl.TsdlPeriodImpl;
import org.tsdl.infrastructure.model.impl.TsdlPeriodSetImpl;

/**
 * A custom deserializer that allows mapping JSON representations of {@link QueryResultDto} instances based on their {@link QueryResultDto#getType()}
 * property.
 */
class QueryResultDtoDeserializer extends StdDeserializer<QueryResultDto> {
  public QueryResultDtoDeserializer() {
    this(null);
  }

  protected QueryResultDtoDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public QueryResultDto deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
    var jsonObject = jp.getCodec().readTree(jp);

    var typeString = ((TextNode) jsonObject.get("type")).asText();
    var type = QueryResultType.valueOf(typeString);
    var targetImplementation = switch (type) {
      case DATA_POINTS -> TsdlDataPointsImpl.class;
      case PERIOD_SET -> TsdlPeriodSetImpl.class;
      case PERIOD -> TsdlPeriodImpl.class;
      case SCALAR -> SingularScalarResultImpl.class;
      case SCALAR_LIST -> MultipleScalarResultImpl.class;
    };

    var resultString = jsonObject.get("result").toString();
    var queryResult = ClientCommons.OBJECT_MAPPER.readValue(resultString, targetImplementation);
    return new QueryResultDto(queryResult, type);
  }
}
