package org.tsdl.infrastructure.api;

import java.util.Map;

public interface StorageServiceConfiguration<T extends Enum<T>> {

    boolean isPropertySet(T property);

    Object getProperty(T property);

    <V> V getProperty(T property, Class<V> targetType);

    Object setProperty(T property, Object value);

    Object unsetProperty(T property);

    Map<T, Class<?>> getSupportedProperties();

    Map<T, Object> getSetProperties();
}
