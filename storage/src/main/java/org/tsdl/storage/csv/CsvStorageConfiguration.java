package org.tsdl.storage.csv;

import java.util.List;
import java.util.Map;
import org.tsdl.infrastructure.api.EnumStorageConfiguration;
import org.tsdl.infrastructure.api.StorageProperty;

public final class CsvStorageConfiguration extends EnumStorageConfiguration {
  public CsvStorageConfiguration(Map<StorageProperty, Object> properties) {
    super(properties);
  }

  public CsvStorageConfiguration() {
    super();
  }

  @Override
  public List<StorageProperty> getSupportedProperties() {
    return List.of(CsvStorageProperty.values());
  }
}
