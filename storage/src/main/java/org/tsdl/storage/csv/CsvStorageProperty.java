package org.tsdl.storage.csv;

import org.tsdl.infrastructure.api.StorageProperty;

public enum CsvStorageProperty implements StorageProperty {
    /**
     * load
     */
    FILE_PATH("filePath", String.class),

    /**
     * load
     */
    FIELD_SEPARATOR("fieldSeparator", Character.class),

    /**
     * transform
     */
    TIME_COLUMN("timeColumn", Integer.class),

    /**
     * transform
     */
    TIME_FORMAT("timeFormat", String.class),

    /**
     * transform
     */
    VALUE_COLUMN("valueColumn", Integer.class),

    /**
     * transform
     * number of rows to skip at beginning
     */
    SKIP_HEADERS("skipHeaders", Integer.class);

    private final String identifier;

    private final Class<?> type;

    CsvStorageProperty(String identifier, Class<?> type) {
        this.identifier = identifier;
        this.type = type;
    }

    @Override
    public String identifier() {
        return identifier;
    }

    @Override
    public Class<?> type() {
        return type;
    }
}
