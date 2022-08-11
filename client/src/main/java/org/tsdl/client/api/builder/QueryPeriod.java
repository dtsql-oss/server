package org.tsdl.client.api.builder;

import java.time.Instant;

/**
 * Represents a period referenced in a TSDL query.
 */
public interface QueryPeriod {
  Instant start();

  Instant end();
}
