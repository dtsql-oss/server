package org.tsdl.infrastructure.model;

import java.util.List;
import org.tsdl.infrastructure.model.impl.TsdlQueryResult;

public interface QueryResult {

  static QueryResult of(List<DataPoint> items) {
    return new TsdlQueryResult(items);
  }

  List<DataPoint> getItems();
}
