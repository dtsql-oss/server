package org.tsdl.storage.csv;

import org.tsdl.infrastructure.api.StorageProperty;

/**
 * Container for properties belonging to {@link CsvStorageConfiguration}.
 */
public enum CsvStorageProperty implements StorageProperty {
  /**
   * Used by load.
   */
  FILE_PATH("filePath", String.class),

  /**
   * Used by load.
   */
  FIELD_SEPARATOR("fieldSeparator", Character.class),

  /**
   * Used by transform.
   */
  TIME_COLUMN("timeColumn", Integer.class),

  /**
   * Used by transform.
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
