package org.tsdl.client.api.builder;

import java.util.List;

/**
 * Represents a (conjunctive or disjunctive) event connective in a TSDL query.
 */
public interface EventConnectiveSpecification {
  /**
   * Logical connective type.
   */
  enum ConnectiveType {
    AND, OR
  }

  List<? extends EventFunctionSpecification> events();

  ConnectiveType type();
}
