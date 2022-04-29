package org.tsdl.infrastructure.api;

import java.util.Map;

public interface StorageServiceConfiguration<T extends Enum<T>> {
    Object getProperty(T property);

    Object setProperty(T property, Object value);

    Map<T, Class<?>> getProperties();
}
