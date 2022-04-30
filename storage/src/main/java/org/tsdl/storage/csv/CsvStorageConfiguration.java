package org.tsdl.storage.csv;

import org.tsdl.infrastructure.api.AbstractStorageConfiguration;

import java.util.Map;

public class CsvStorageConfiguration extends AbstractStorageConfiguration<CsvStorageProperty> {
    private static final Map<CsvStorageProperty, Class<?>> PROPERTY_TYPES = Map.of(
      CsvStorageProperty.FILE_PATH, String.class,
      CsvStorageProperty.FIELD_SEPARATOR, Character.class,
      CsvStorageProperty.VALUE_COLUMN, Integer.class,
      CsvStorageProperty.TIME_COLUMN, Integer.class,
      CsvStorageProperty.TIME_FORMAT, String.class,
      CsvStorageProperty.SKIP_HEADERS, Integer.class
    );

    public CsvStorageConfiguration(Map<CsvStorageProperty, Object> properties) {
        super(properties);
    }

    public CsvStorageConfiguration() {
        super();
    }

    @Override
    public Map<CsvStorageProperty, Class<?>> getSupportedProperties() {
        return PROPERTY_TYPES;
    }

    @Override
    protected Class<CsvStorageProperty> getPropertiesEnumClass() {
        return CsvStorageProperty.class;
    }
}
