package org.tsdl.storage.csv;

public enum CsvStorageProperty {
    /**
     * load
     */
    FILE_PATH,

    /**
     * load
     */
    FIELD_SEPARATOR,

    /**
     * transform
     */
    TIME_COLUMN,

    /**
     * transform
     */
    TIME_FORMAT,

    /**
     * transform
     */
    VALUE_COLUMN,

    /**
     * transform
     * number of rows to skip at beginning
     */
    SKIP_HEADERS

}
