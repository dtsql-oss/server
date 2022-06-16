package org.tsdl.storage.influxdb;

import java.time.Instant;
import org.tsdl.infrastructure.api.StorageProperty;

/**
 * Container for properties belonging to {@link InfluxDbStorageConfiguration}.
 */
public enum InfluxDbStorageProperty implements StorageProperty {
  /**
   * Used by initialize.
   */
  TOKEN("token", char[].class),

  /**
   * Used by initialize.
   */
  ORGANIZATION("organization", String.class),

  /**
   * Used by store, load.
   */
  BUCKET("bucket", String.class),

  /**
   * Used by initialize.
   */
  URL("url", String.class),

  /**
   * Used by load.
   */
  QUERY("query", String.class),

  /**
   * Used by load.
   */
  LOAD_FROM("loadFrom", Instant.class),

  /*
   * Used by load.
   */
  LOAD_UNTIL("loadUntil", Instant.class),

  /**
   * <p>
   * Used by transform.
   * </p>
   * <p>
   * -1: take values from all tables
   * >= 0: index of table to take values from
   * </p>
   */
  TABLE_INDEX("tableIndex", Integer.class);

  private final String identifier;

  private final Class<?> type;

  InfluxDbStorageProperty(String identifier, Class<?> type) {
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
