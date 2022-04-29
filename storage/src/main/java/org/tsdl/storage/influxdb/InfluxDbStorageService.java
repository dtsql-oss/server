package org.tsdl.storage.influxdb;

import org.tsdl.infrastructure.api.StorageService;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

public class InfluxDbStorageService implements StorageService<Object, InfluxDbStorageConfiguration, InfluxDbStorageProperty> {
    private InfluxDbStorageConfiguration configuration;

    @Override
    public void initialize(InfluxDbStorageConfiguration serviceConfiguration) {
        this.configuration = serviceConfiguration;
    }

    @Override
    public boolean isInitialized() {
        return configuration != null; // TODO check if connection to influxdb has been established
    }

    @Override
    public void store(InfluxDbStorageConfiguration storageConfiguration) {
        Conditions.check(Condition.STATE, isInitialized(), "InfluxDB configuration must not be null. Call initialize() beforehand.");
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Iterable<Object> load(InfluxDbStorageConfiguration lookupConfiguration) {
        Conditions.check(Condition.STATE, isInitialized(), "InfluxDB configuration must not be null. Call initialize() beforehand.");
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
