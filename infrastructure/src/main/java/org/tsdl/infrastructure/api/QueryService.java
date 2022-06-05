package org.tsdl.infrastructure.api;

import java.util.List;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.infrastructure.model.QueryResult;

public interface QueryService {
  QueryResult query(List<DataPoint> data, String query);
}
