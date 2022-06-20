package org.tsdl.storage.csv;

import org.tsdl.infrastructure.api.StorageProperty;

/**
 * Container for properties belonging to {@link CsvStorageConfiguration}.
 */
public enum CsvStorageProperty implements StorageProperty {
  /**
   * Used by load, store.
   */
  FILE_PATH("filePath", String.class),

  /**
   * Used by load, store.
   */
  FIELD_SEPARATOR("fieldSeparator", Character.class),

  /**
   * Used by transform.
   */
  TIME_COLUMN("timeColumn", Integer.class),

  /**
   * Used by transform, store.
   */
  TIME_FORMAT("timeFormat", String.class),

  /**
   * Used by transform.
   */
  VALUE_COLUMN("valueColumn", Integer.class),

  /**
   * <p>
   * Used by transform.
   * </p>
   * <p>
   * Specifies the number of rows to skip before the actual data starts.
   * </p>
   */
  SKIP_HEADERS("skipHeaders", Integer.class),

  /*
   * Used by store.
   */
  APPEND("append", Boolean.class),

  /**
   * Used by store.
   */
  TIME_COLUMN_LABEL("timeColumnLabel", String.class),

  /*
   * Used by store.
   */
  VALUE_COLUMN_LABEL("valueColumnLabel", String.class),

  /**
   * Used by store.
   */
  INCLUDE_HEADERS("timeColumnLabel", Boolean.class);

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
