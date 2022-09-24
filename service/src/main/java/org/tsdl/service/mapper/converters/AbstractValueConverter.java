package org.tsdl.service.mapper.converters;

import java.util.Map;
import java.util.function.Function;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

public abstract class AbstractValueConverter<T> implements PropertyValueConverter<T> {
  protected abstract Map<Class<?>, Function<Object, T>> converters();

  @Override
  public T convert(Object value) {
    Conditions.checkNotNull(Condition.ARGUMENT, value, "Value must not be null.");
    return converters().get(value.getClass()).apply(value);
  }

  @Override
  public boolean canConvert(Object value, Class<?> targetClass) {
    Conditions.checkNotNull(Condition.ARGUMENT, value, "Value must not be null.");
    var valueClass = value.getClass();

    var canConvert = supportedInputTypes().contains(valueClass);
    Conditions.checkEquals(Condition.STATE, canConvert, converters().containsKey(valueClass), "There is an internal discrepancy for type '%s'",
        valueClass.getName());

    return canConvert && targetClass.isAssignableFrom(targetType());
  }
}
