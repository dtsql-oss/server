package org.tsdl.storage.csv;

import org.tsdl.storage.AbstractStorageConfiguration;

import java.util.Map;

public class CsvStorageConfiguration extends AbstractStorageConfiguration<CsvStorageProperty> {
    @Override
    public Map<CsvStorageProperty, Class<?>> getPropertyTypes() {
        return Map.of(

        );
    }

    @Override
    public Class<CsvStorageProperty> getPropertiesEnumClass() {
        return CsvStorageProperty.class;
    }
}
