package org.tsdl.storage.influxdb;

import org.tsdl.infrastructure.api.AbstractStorageConfiguration;

import java.time.Instant;
import java.util.Map;

public class InfluxDbStorageConfiguration extends AbstractStorageConfiguration<InfluxDbStorageProperty> {
    private static final Map<InfluxDbStorageProperty, Class<?>> PROPERTY_TYPES = Map.of(
      InfluxDbStorageProperty.TOKEN, char[].class,
      InfluxDbStorageProperty.ORGANIZATION, String.class,
      InfluxDbStorageProperty.BUCKET, String.class,
      InfluxDbStorageProperty.URL, String.class,
      InfluxDbStorageProperty.QUERY, String.class,
      InfluxDbStorageProperty.LOAD_FROM, Instant.class,
      InfluxDbStorageProperty.LOAD_UNTIL, Instant.class,
      InfluxDbStorageProperty.TABLE_INDEX, Integer.class
    );

    public InfluxDbStorageConfiguration(Map<InfluxDbStorageProperty, Object> properties) {
        super(properties);
    }

    public InfluxDbStorageConfiguration() {
        super();
    }

    @Override
    public Map<InfluxDbStorageProperty, Class<?>> getSupportedProperties() {
        return PROPERTY_TYPES;
    }

    @Override
    protected Class<InfluxDbStorageProperty> getPropertiesEnumClass() {
        return InfluxDbStorageProperty.class;
    }
}
