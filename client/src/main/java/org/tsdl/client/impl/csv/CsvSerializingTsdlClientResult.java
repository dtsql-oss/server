package org.tsdl.client.impl.csv;

import org.tsdl.client.api.QueryClientResult;
import org.tsdl.infrastructure.model.QueryResult;

/**
 * A {@link QueryClientResult} specific to {@link CsvSerializingTsdlClient}, containing the path of the cache file containing the query result.
 */
public record CsvSerializingTsdlClientResult(QueryResult queryResult, String resultCacheFilePath) implements QueryClientResult {
}
