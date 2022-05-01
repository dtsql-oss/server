package org.tsdl.infrastructure.api;

import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

import java.util.Arrays;
import java.util.NoSuchElementException;

public interface StorageProperty {
    String identifier();

    Class<?> type();

    // no compiler validation of StorageProperty because corresponding compiler wildcards are impossible (enum of unknown type + implementing interface)
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
}
