package org.tsdl.infrastructure.api;

import java.util.List;
import java.util.Map;

/**
 * Represents a container for the configuration (properties) of a {@link StorageService}.
 */
public interface StorageServiceConfiguration {

  boolean isPropertySet(StorageProperty property);

  Object getProperty(StorageProperty property);

  <V> V getProperty(StorageProperty property, Class<V> targetType);

  Object setProperty(StorageProperty property, Object value);

  Object unsetProperty(StorageProperty property);

  List<StorageProperty> getSupportedProperties();

  Map<StorageProperty, Object> getSetProperties();
}
