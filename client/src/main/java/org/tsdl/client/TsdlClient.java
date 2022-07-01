package org.tsdl.client;

import org.tsdl.infrastructure.model.QueryResult;

/**
 * Represents a client for the TSDL API.
 *
 * @param <T> implementation-dependent configuration/specification to be used when performing TSDL requests and handling responses
 */
public interface TsdlClient {
  QueryClientResult query(QueryClientSpecification querySpecification);

  QueryResult query(String filePath);
}