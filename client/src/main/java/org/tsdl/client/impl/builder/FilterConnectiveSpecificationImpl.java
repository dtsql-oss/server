package org.tsdl.client.impl.builder;

import java.util.Arrays;
import java.util.List;
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
  public ConnectiveType type() {
    return type;
  }

  public static FilterConnectiveSpecification and(List<FilterSpecification> filters) {
    return new FilterConnectiveSpecificationImpl(filters, ConnectiveType.AND);
  }

  public static FilterConnectiveSpecification and(FilterSpecification... filters) {
    return new FilterConnectiveSpecificationImpl(Arrays.stream(filters).toList(), ConnectiveType.AND);
  }

  public static FilterConnectiveSpecification or(List<FilterSpecification> filters) {
    return new FilterConnectiveSpecificationImpl(filters, ConnectiveType.OR);
  }

  public static FilterConnectiveSpecification or(FilterSpecification... filters) {
    return new FilterConnectiveSpecificationImpl(Arrays.stream(filters).toList(), ConnectiveType.OR);
  }
}
