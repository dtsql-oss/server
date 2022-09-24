package org.tsdl.client.api;

import org.tsdl.infrastructure.model.QueryResult;

/**
 * Represents components being able to write specific instances of {@link QueryResult} subtypes - e.g., serializing to a file.
 */
public interface QueryResultWriter {
  void write(QueryResult result, QueryClientSpecification specification, String targetFile);
}
