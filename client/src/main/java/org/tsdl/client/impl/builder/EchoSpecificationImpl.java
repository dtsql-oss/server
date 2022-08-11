package org.tsdl.client.impl.builder;

import java.util.Arrays;
import java.util.List;
import org.tsdl.client.api.builder.EchoSpecification;

/**
 * Default implementation of {@link EchoSpecification}
 */
public final class EchoSpecificationImpl implements EchoSpecification {
  private final List<String> arguments;

  private EchoSpecificationImpl(List<String> arguments) {
    this.arguments = arguments;
  }

  @Override
  public List<String> arguments() {
    return arguments;
  }

  public static EchoSpecification echo() {
    return echo(List.of());
  }

  public static EchoSpecification echo(String... arguments) {
    return echo(Arrays.stream(arguments).toList());
  }

  public static EchoSpecification echo(List<String> arguments) {
    return new EchoSpecificationImpl(arguments);
  }
}
