package org.tsdl.service.mapper.converters;

import java.util.Set;

/**
 * Provides methods to convert values, received from JSON to other values.
 *
 * @param <T> The target type of the respective implementation.
 */
public interface PropertyValueConverter<T> {
  Set<Class<?>> supportedInputTypes();

  Class<T> targetType();

  T convert(Object value);

  boolean canConvert(Object value, Class<?> targetClass);
}
