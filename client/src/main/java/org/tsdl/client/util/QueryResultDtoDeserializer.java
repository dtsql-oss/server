package org.tsdl.client.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.io.IOException;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.dto.QueryResultDto;
import org.tsdl.infrastructure.model.QueryResult;
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
    Class<? extends QueryResult> targetImplementation;
    switch (type) {
      case DATA_POINTS:
        targetImplementation = TsdlDataPointsImpl.class;
        break;
      case PERIOD_SET:
        targetImplementation = TsdlPeriodSetImpl.class;
        break;
      case PERIOD:
        targetImplementation = TsdlPeriodImpl.class;
        break;
      case SCALAR:
        targetImplementation = SingularScalarResultImpl.class;
        break;
      case SCALAR_LIST:
        targetImplementation = MultipleScalarResultImpl.class;
        break;
      default:
        throw Conditions.exception(Condition.ARGUMENT, "Unknown query result type '%s'", type);
    }

    var resultNode = requireNode(jp, jsonObject, "result");

    var resultString = resultNode.toString();
    var queryResult = ClientCommons.OBJECT_MAPPER.readValue(resultString, targetImplementation);
    return new QueryResultDto(queryResult, type);
  }

  @SuppressWarnings("unchecked") // type safety is ensured by isAssignableFrom() check
  private <T extends TreeNode> T requireNode(JsonParser jp, TreeNode obj, String key, Class<T> requiredType) throws JsonMappingException {
    var node = obj.get(key);
    if (node == null || node.size() == 0 || node instanceof NullNode) {
      throw new JsonMappingException(jp, "Could not extract query result from \"result\" key for QueryResultDto.");
    }

    var type = requiredType != null ? requiredType : TreeNode.class;

    if (!type.isAssignableFrom(node.getClass())) {
      throw new JsonMappingException(jp, String.format("Node type \"%s\" is not assignable to \"%s\".", node.getClass().getName(), type.getName()));
    }

    return (T) node;
  }

  private TreeNode requireNode(JsonParser jp, TreeNode obj, String key) throws JsonMappingException {
    return requireNode(jp, obj, key, null);
  }
}
