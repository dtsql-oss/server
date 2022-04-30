package org.tsdl.storage.csv;

import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

import java.util.EnumSet;
import java.util.NoSuchElementException;

public enum CsvStorageProperty {
    /**
     * load
     */
    FILE_PATH("filePath"),

    /**
     * load
     */
    FIELD_SEPARATOR("fieldSeparator"),

    /**
     * transform
     */
    TIME_COLUMN("timeColumn"),

    /**
     * transform
     */
    TIME_FORMAT("timeFormat"),

    /**
     * transform
     */
    VALUE_COLUMN("valueColumn"),

    /**
     * transform
     * number of rows to skip at beginning
     */
    SKIP_HEADERS("skipHeaders");

    private final String identifier;

    CsvStorageProperty(String identifier) {
        this.identifier = identifier;
    }

    public String identifier() {
        return identifier;
    }

    public static CsvStorageProperty fromIdentifier(String identifier) {
        Conditions.checkNotNull(Condition.ARGUMENT, identifier, "Identifier must not be null.");
        return EnumSet.allOf(CsvStorageProperty.class).stream()
          .filter(element -> identifier.equals(element.identifier))
          .findFirst()
          .orElseThrow(() -> new NoSuchElementException("There is no CsvStorageProperty member with identifier '%s'.".formatted(identifier)));
    }
}
