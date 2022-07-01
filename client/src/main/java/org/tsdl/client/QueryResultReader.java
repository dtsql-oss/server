package org.tsdl.client;

import org.tsdl.infrastructure.model.QueryResult;

/**
 * Represents components being able to read specific instances of {@link QueryResult} subtypes - e.g., deserializing from a file.
 */
public interface QueryResultReader {
  QueryResult read(String filePath);
}
