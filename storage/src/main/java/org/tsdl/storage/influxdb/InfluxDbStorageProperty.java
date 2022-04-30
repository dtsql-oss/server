package org.tsdl.storage.influxdb;

import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

import java.util.EnumSet;
import java.util.NoSuchElementException;

public enum InfluxDbStorageProperty {
    /**
     * initialize
     */
    TOKEN("token"),

    /**
     * initialize
     */
    ORGANIZATION("organization"),

    /**
     * store, load
     */
    BUCKET("bucket"),

    /**
     * initialize
     */
    URL("url"),

    /**
     * load
     */
    QUERY("query"),

    /**
     * load
     */
    LOAD_FROM("loadFrom"),

    /*
     * load
     */
    LOAD_UNTIL("loadUntil"),

    /**
     * transform
     * -1: take values from all tables
     * >= 0: index of table to take values from
     */
    TABLE_INDEX("tableIndex");

    private final String identifier;

    InfluxDbStorageProperty(String identifier) {
        this.identifier = identifier;
    }

    public String identifier() {
        return identifier;
    }

    public static InfluxDbStorageProperty fromIdentifier(String identifier) {
        Conditions.checkNotNull(Condition.ARGUMENT, identifier, "Identifier must not be null.");
        return EnumSet.allOf(InfluxDbStorageProperty.class).stream()
          .filter(element -> identifier.equals(element.identifier))
          .findFirst()
          .orElseThrow(() -> new NoSuchElementException("There is no InfluxDbStorageProperty member with identifier '%s'.".formatted(identifier)));
    }
}
