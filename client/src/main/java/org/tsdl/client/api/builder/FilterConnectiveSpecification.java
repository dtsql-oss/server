package org.tsdl.client.api.builder;

import java.util.List;

/**
 * Represents a (conjunctive or disjunctive) filter connective in a TSDL query.
 */
public interface FilterConnectiveSpecification extends EventConnectiveSpecification {
  List<FilterSpecification> filters();

  ConnectiveType type();
}
