package org.tsdl.client.impl.csv;

import org.tsdl.client.api.QueryClientSpecification;
import org.tsdl.infrastructure.dto.QueryDto;

/**
 * A {@link QueryClientSpecification} specific to {@link CsvSerializingTsdlClient}.
 */
public record CsvSerializingQueryClientSpecification(
    QueryDto query,
    String serverUrl
) implements QueryClientSpecification {
}
