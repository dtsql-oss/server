package org.tsdl.client.impl.builder;

import java.util.Optional;
import org.tsdl.client.api.builder.EventSpecification;
import org.tsdl.client.api.builder.FilterConnectiveSpecification;
import org.tsdl.client.api.builder.Range;

/**
 * Default implementation of {@link EventSpecification}.
 */
public final class EventSpecificationImpl implements EventSpecification {
  private final FilterConnectiveSpecification definition;
  private final Range duration;
  private final String identifier;

  private EventSpecificationImpl(FilterConnectiveSpecification definition, Range duration, String identifier) {
    this.definition = definition;
    this.duration = duration;
    this.identifier = identifier;
  }

  @Override
  public FilterConnectiveSpecification definition() {
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

  public static EventSpecificationImpl event(FilterConnectiveSpecification definition, Range duration, String identifier) {
    return new EventSpecificationImpl(definition, duration, identifier);
  }

  public static EventSpecificationImpl event(FilterConnectiveSpecification definition, String identifier) {
    return new EventSpecificationImpl(definition, null, identifier);
  }

}
