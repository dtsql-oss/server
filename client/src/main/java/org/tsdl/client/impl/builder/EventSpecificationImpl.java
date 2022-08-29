package org.tsdl.client.impl.builder;

import java.util.Optional;
import org.tsdl.client.api.builder.EventConnectiveSpecification;
import org.tsdl.client.api.builder.EventSpecification;
import org.tsdl.client.api.builder.Range;

/**
 * Default implementation of {@link EventSpecification}.
 */
public final class EventSpecificationImpl implements EventSpecification {
  private final EventConnectiveSpecification definition;
  private final Range duration;
  private final String identifier;

  private EventSpecificationImpl(EventConnectiveSpecification definition, Range duration, String identifier) {
    this.definition = definition;
    this.duration = duration;
    this.identifier = identifier;
  }

  @Override
  public EventConnectiveSpecification definition() {
    return definition;
  }

  @Override
  public Optional<Range> duration() {
    return Optional.ofNullable(duration);
  }

  @Override
  public String identifier() {
    return identifier;
  }

  public static EventSpecificationImpl event(EventConnectiveSpecification definition, Range duration, String identifier) {
    return new EventSpecificationImpl(definition, duration, identifier);
  }

  public static EventSpecificationImpl event(EventConnectiveSpecification definition, String identifier) {
    return new EventSpecificationImpl(definition, null, identifier);
  }

}
