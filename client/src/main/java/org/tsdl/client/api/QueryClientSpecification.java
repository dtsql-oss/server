package org.tsdl.client.api;

import org.tsdl.infrastructure.dto.QueryDto;

/**
 * Represents input objects to {@link TsdlClient} implementations.
 */
public interface QueryClientSpecification {
  QueryDto query();

  String serverUrl();
}
