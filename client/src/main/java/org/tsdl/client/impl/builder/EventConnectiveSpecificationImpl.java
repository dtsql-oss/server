package org.tsdl.client.impl.builder;

import java.util.Arrays;
import java.util.List;
import org.tsdl.client.api.builder.EventConnectiveSpecification;
import org.tsdl.client.api.builder.EventFunctionSpecification;
import org.tsdl.client.api.builder.FilterConnectiveSpecification;
import org.tsdl.client.api.builder.FilterSpecification;

/**
 * Default implementation of {@link EventConnectiveSpecification}.
 */
public final class EventConnectiveSpecificationImpl implements EventConnectiveSpecification {
  private final List<? extends EventFunctionSpecification> events;
  private final ConnectiveType type;

  private EventConnectiveSpecificationImpl(List<? extends EventFunctionSpecification> events, ConnectiveType type) {
    this.events = events;
    this.type = type;
  }

  @Override
  public List<? extends EventFunctionSpecification> events() {
    return events;
  }

  @Override
  public ConnectiveType type() {
    return type;
  }

  public static EventConnectiveSpecification and(List<? extends EventFunctionSpecification> events) {
    return new EventConnectiveSpecificationImpl(events, ConnectiveType.AND);
  }

  public static EventConnectiveSpecification and(EventFunctionSpecification... events) {
    return and(Arrays.stream(events).toList());
  }

  public static FilterConnectiveSpecification and(FilterSpecification... filters) {
    return FilterConnectiveSpecificationImpl.and(filters);
  }

  public static EventConnectiveSpecification or(List<? extends EventFunctionSpecification> events) {
    return new EventConnectiveSpecificationImpl(events, ConnectiveType.OR);
  }

  public static EventConnectiveSpecification or(EventFunctionSpecification... events) {
    return new EventConnectiveSpecificationImpl(Arrays.stream(events).toList(), ConnectiveType.OR);
  }

  public static FilterConnectiveSpecification or(FilterSpecification... filters) {
    return FilterConnectiveSpecificationImpl.or(filters);
  }
}
