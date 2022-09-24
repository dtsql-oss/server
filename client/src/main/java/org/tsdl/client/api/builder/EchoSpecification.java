package org.tsdl.client.api.builder;

import java.util.List;

/**
 * Represents an 'echo' statement in a TSDL query.
 */
public interface EchoSpecification {
  List<String> arguments();
}
