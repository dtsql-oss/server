package org.tsdl.service.mapper.converters;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

public class CharacterValueConverter extends AbstractValueConverter<Character> {
  private static final Set<Class<?>> SUPPORTED_INPUT_TYPES = Set.of(Character.class, String.class);
  private static final Map<Class<?>, Function<Object, Character>> CONVERTERS = Map.of(
      Character.class, char.class::cast,
      String.class, v -> {
        var input = (String) v;
        Conditions.checkIsTrue(Condition.ARGUMENT, input.length() == 1, "String to map to character must be of length 1.");
        return input.charAt(0);
      }
  );

  @Override
  public Set<Class<?>> supportedInputTypes() {
    return SUPPORTED_INPUT_TYPES;
  }

  @Override
  protected Map<Class<?>, Function<Object, Character>> converters() {
    return CONVERTERS;
  }

  @Override
  public Class<Character> targetType() {
    return Character.class;
  }
}
