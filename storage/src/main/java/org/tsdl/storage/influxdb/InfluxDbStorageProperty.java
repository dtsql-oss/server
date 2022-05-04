package org.tsdl.storage.influxdb;

import org.tsdl.infrastructure.api.StorageProperty;

import java.time.Instant;

public enum InfluxDbStorageProperty implements StorageProperty {
    /**
     * initialize
     */
    TOKEN("token", char[].class),

    /**
     * initialize
     */
    ORGANIZATION("organization", String.class),

    /**
     * store, load
     */
    BUCKET("bucket", String.class),

    /**
     * initialize
     */
    URL("url", String.class),

    /**
     * load
     */
    QUERY("query", String.class),

    /**
     * load
     */
    LOAD_FROM("loadFrom", Instant.class),

    /*
     * load
     */
    LOAD_UNTIL("loadUntil", Instant.class),

    /**
     * transform
     * -1: take values from all tables
     * >= 0: index of table to take values from
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
