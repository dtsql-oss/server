package org.tsdl.infrastructure.api;

import java.util.List;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.infrastructure.model.QueryResult;

/**
 * Provides methods for extracting information from time series with TSDL queries.
 */
public interface QueryService {
  /**
   * Precondition: data is sorted by date-time in ascending order.
   */
  QueryResult query(List<DataPoint> data, String query);
}
