package org.tsdl.service.mapper.converters;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class StringArrayValueConverter extends AbstractValueConverter<String[]> {
  private static final Set<Class<?>> SUPPORTED_INPUT_TYPES = Set.of(String[].class, ArrayList.class);

  private static final Map<Class<?>, Function<Object, String[]>> CONVERTERS = Map.of(
      String[].class, String[].class::cast,
      ArrayList.class, v -> ((ArrayList<?>) v).stream()
          .filter(o -> o instanceof String)
          .map(String.class::cast)
          .toArray(String[]::new)
  );

  @Override
  protected Map<Class<?>, Function<Object, String[]>> converters() {
    return CONVERTERS;
  }

  @Override
  public Set<Class<?>> supportedInputTypes() {
    return SUPPORTED_INPUT_TYPES;
  }

  @Override
  public Class<String[]> targetType() {
    return String[].class;
  }
}
