package org.tsdl.client.api;

import org.tsdl.infrastructure.model.QueryResult;

/**
 * Represents a client for the TSDL API.
 */
public interface TsdlClient {
  QueryClientResult query(QueryClientSpecification querySpecification);

  QueryResult query(String filePath, Object arg1, Object arg2); // TODO how to make new queries? what API is needed?
}