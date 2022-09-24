package org.tsdl.client.impl.builder;

import java.util.Arrays;
import java.util.List;
import org.tsdl.client.api.builder.EventFunctionSpecification;
import org.tsdl.client.api.builder.FilterConnectiveSpecification;
import org.tsdl.client.api.builder.FilterSpecification;

/**
 * Default implementation of {@link FilterConnectiveSpecification}.
 */
public final class FilterConnectiveSpecificationImpl implements FilterConnectiveSpecification {
  private final List<FilterSpecification> filters;
  private final ConnectiveType type;

  private FilterConnectiveSpecificationImpl(List<FilterSpecification> filters, ConnectiveType type) {
    this.filters = filters;
    this.type = type;
  }

  @Override
  public List<FilterSpecification> filters() {
    return filters;
  }

  @Override
  public List<? extends EventFunctionSpecification> events() {
    return filters;
  }

  @Override
  public ConnectiveType type() {
    return type;
  }

  static FilterConnectiveSpecification and(List<FilterSpecification> filters) {
    return new FilterConnectiveSpecificationImpl(filters, ConnectiveType.AND);
  }

  static FilterConnectiveSpecification and(FilterSpecification... filters) {
    return and(Arrays.stream(filters).toList());
  }

  static FilterConnectiveSpecification or(List<FilterSpecification> filters) {
    return new FilterConnectiveSpecificationImpl(filters, ConnectiveType.OR);
  }

  static FilterConnectiveSpecification or(FilterSpecification... filters) {
    return or(Arrays.stream(filters).toList());
  }
}
