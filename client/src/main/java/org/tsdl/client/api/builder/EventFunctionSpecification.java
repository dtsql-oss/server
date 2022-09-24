package org.tsdl.client.api.builder;

/**
 * Represents an event function - either filter or complex.
 */
public interface EventFunctionSpecification {
  boolean isNegated();

  EventFunctionSpecification negate();

  @SuppressWarnings("unchecked")
  static <T extends EventFunctionSpecification> T not(T event) {
    return (T) event.negate();
  }
}
