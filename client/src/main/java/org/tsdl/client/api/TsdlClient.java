package org.tsdl.client.api;

/**
 * Represents a client for the TSDL API.
 */
public interface TsdlClient {
  QueryClientResult query(QueryClientSpecification querySpecification);

  QueryClientResult query(String cachedTimeSeries, String queryEndpoint, String tsdlQuery);
}