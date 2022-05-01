package org.tsdl.storage.influxdb;

import org.tsdl.infrastructure.api.EnumStorageConfiguration;
import org.tsdl.infrastructure.api.StorageProperty;

import java.util.List;
import java.util.Map;

public final class InfluxDbStorageConfiguration extends EnumStorageConfiguration {
    public InfluxDbStorageConfiguration(Map<StorageProperty, Object> properties) {
        super(properties);
    }

    public InfluxDbStorageConfiguration() {
        super();
    }

    @Override
    public List<StorageProperty> getSupportedProperties() {
        return List.of(InfluxDbStorageProperty.values());
    }
}
