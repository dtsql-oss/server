package org.tsdl.storage.csv;

import org.tsdl.storage.AbstractStorageConfiguration;

import java.util.Map;

public class CsvStorageConfiguration extends AbstractStorageConfiguration<CsvStorageProperty> {
    private static final Map<CsvStorageProperty, Class<?>> PROPERTY_TYPES = Map.of(
      CsvStorageProperty.FILE_PATH, String.class,
      CsvStorageProperty.FIELD_SEPARATOR, Character.class
    );

    public CsvStorageConfiguration(Map<CsvStorageProperty, Object> properties) {
        super(properties);
    }

    public CsvStorageConfiguration() {
        super();
    }

    @Override
    public Map<CsvStorageProperty, Class<?>> getPropertyTypes() {
        return PROPERTY_TYPES;
    }

    @Override
    public Class<CsvStorageProperty> getPropertiesEnumClass() {
        return CsvStorageProperty.class;
    }
}
