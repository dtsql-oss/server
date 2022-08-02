package org.tsdl.client.api;

import org.tsdl.infrastructure.model.QueryResult;

/**
 * Common interface representing values returned by a {@link TsdlClient#query(QueryClientSpecification)} implementation.
 */
public interface QueryClientResult {
  QueryResult queryResult();
}
