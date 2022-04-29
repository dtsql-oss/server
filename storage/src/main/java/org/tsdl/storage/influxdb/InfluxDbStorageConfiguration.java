package org.tsdl.storage.influxdb;

import org.tsdl.storage.AbstractStorageConfiguration;

import java.util.Map;

public class InfluxDbStorageConfiguration extends AbstractStorageConfiguration<InfluxDbStorageProperty> {

    public InfluxDbStorageConfiguration(Map<InfluxDbStorageProperty, Object> properties) {
        super(properties);
    }

    public InfluxDbStorageConfiguration() {
        super();
    }

    @Override
    public Map<InfluxDbStorageProperty, Class<?>> getPropertyTypes() {
        return Map.of(
          InfluxDbStorageProperty.TOKEN, String.class,
          InfluxDbStorageProperty.ORGANIZATION, String.class,
          InfluxDbStorageProperty.BUCKET, String.class,
          InfluxDbStorageProperty.ENDPOINT, String.class
        );
    }

    @Override
    public Class<InfluxDbStorageProperty> getPropertiesEnumClass() {
        return InfluxDbStorageProperty.class;
    }
}
