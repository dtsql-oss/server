package org.tsdl.service.mapper.converters;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class CharacterArrayValueConverter extends AbstractValueConverter<char[]> {
  private static final Set<Class<?>> SUPPORTED_INPUT_TYPES = Set.of(char[].class, String.class);
  private static final Map<Class<?>, Function<Object, char[]>> CONVERTERS = Map.of(
      char[].class, char[].class::cast,
      String.class, v -> ((String) v).toCharArray()
  );

  @Override
  protected Map<Class<?>, Function<Object, char[]>> converters() {
    return CONVERTERS;
  }

  @Override
  public Set<Class<?>> supportedInputTypes() {
    return SUPPORTED_INPUT_TYPES;
  }

  @Override
  public Class<char[]> targetType() {
    return char[].class;
  }
}
