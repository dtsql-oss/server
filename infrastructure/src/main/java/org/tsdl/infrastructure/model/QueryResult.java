package org.tsdl.infrastructure.model;

import java.util.List;
import org.tsdl.infrastructure.model.impl.TsdlDataPointsImpl;

/**
 * A result of the evaluation process of a TSDL query.
 */
public interface QueryResult {

  QueryResultType type();

  static QueryResult of(List<DataPoint> items) {
    return new TsdlDataPointsImpl(items);
  }
}
