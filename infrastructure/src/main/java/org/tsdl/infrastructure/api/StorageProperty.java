package org.tsdl.infrastructure.api;

import java.util.Arrays;
import java.util.NoSuchElementException;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

/**
 * Represents a configuration property for {@link StorageService}s.
 */
public interface StorageProperty {
  /**
   * Constructs an {@link StorageProperty} instance, more specifically a property which is a member of an {@link EnumStorageConfiguration},
   * from the given {@link StorageProperty#identifier()} (parameter {@code identifier}) and class, as defined by {@code clazz}.
   *
   * @param identifier the identifier of the property to instantiate
   * @param clazz      the class of the property wrapped into an {@link EnumStorageConfiguration} to instantiate.
   * @return a {@link StorageProperty} with identifier {@code identifier} and class {@code clazz}.
   */
  static StorageProperty fromIdentifier(String identifier, Class<? extends Enum<?>> clazz) {
    Conditions.checkNotNull(Condition.ARGUMENT, identifier, "Identifier must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, clazz, "clazz must not be null.");
    Conditions.checkIsTrue(Condition.ARGUMENT, StorageProperty.class.isAssignableFrom(clazz),
        "Type of clazz parameter must be assignable to type StorageProperty.");

    return Arrays.stream(clazz.getEnumConstants())
        .map(StorageProperty.class::cast)
        .filter(element -> identifier.equals(element.identifier()))
        .findFirst()
        .orElseThrow(() -> new NoSuchElementException("There is no '%s' member with identifier '%s'.".formatted(clazz.getTypeName(), identifier)));
  }

  String identifier();

  Class<?> type();
}
