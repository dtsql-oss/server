package org.tsdl.infrastructure.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.tsdl.infrastructure.model.impl.TsdlDataPointsImpl;

/**
 * A result of the evaluation process of a TSDL query that consists of multiple {@link DataPoint} instances.
 */
@JsonDeserialize(as = TsdlDataPointsImpl.class)
public interface TsdlDataPoints extends QueryResult {
  @JsonProperty
  List<DataPoint> items();

  @Override
  default QueryResultType type() {
    return QueryResultType.DATA_POINTS;
  }
}
