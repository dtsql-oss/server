package org.tsdl.client;

/**
 * Represents a client for the TSDL API.
 *
 * @param <T> implementation-dependent configuration/specification to be used when performing TSDL requests and handling responses
 */
public interface TsdlClient<T extends QueryClientSpecification> {
  QueryClientResult query(T querySpecification);
}