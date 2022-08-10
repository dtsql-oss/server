package org.tsdl.client.api.builder;

import java.util.Optional;

/**
 * Represents the "USING EVENTS" section of a TSDL query.
 */
public interface EventSpecification {
  FilterConnectiveSpecification definition();

  Optional<Range> duration();

  String identifier();
}
