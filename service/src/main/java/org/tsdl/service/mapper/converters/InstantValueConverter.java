package org.tsdl.service.mapper.converters;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class InstantValueConverter extends AbstractValueConverter<Instant> {
  private static final Set<Class<?>> SUPPORTED_INPUT_TYPES = Set.of(Instant.class, String.class);
  private static final Map<Class<?>, Function<Object, Instant>> CONVERTERS = Map.of(
      Instant.class, Instant.class::cast,
      String.class, v -> Instant.from(DateTimeFormatter.ISO_INSTANT.parse((String) v))
  );

  @Override
  protected Map<Class<?>, Function<Object, Instant>> converters() {
    return CONVERTERS;
  }

  @Override
  public Set<Class<?>> supportedInputTypes() {
    return SUPPORTED_INPUT_TYPES;
  }

  @Override
  public Class<Instant> targetType() {
    return Instant.class;
  }
}
