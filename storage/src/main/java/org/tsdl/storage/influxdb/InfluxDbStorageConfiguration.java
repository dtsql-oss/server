package org.tsdl.storage.influxdb;

import java.util.List;
import java.util.Map;
import org.tsdl.infrastructure.api.EnumStorageConfiguration;
import org.tsdl.infrastructure.api.StorageProperty;

/**
 * An {@link EnumStorageConfiguration} for configuring {@link InfluxDbStorageConfiguration} instances.
 */
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
