package org.tsdl.storage.influxdb;

public enum InfluxDbStorageProperty {
    /**
     * initialize
     */
    TOKEN,

    /**
     * initialize
     */
    ORGANIZATION,

    /**
     * initialize
     */
    BUCKET,

    /**
     * initialize
     */
    URL,

    /**
     * load
     */
    QUERY,

    /**
     * transform
     * -1: take values from all tables
     * >= 0: index of table to take values from
     */
    TABLE_INDEX
}
