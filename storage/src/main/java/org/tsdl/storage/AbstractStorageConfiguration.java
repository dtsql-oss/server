package org.tsdl.storage;

import org.tsdl.infrastructure.api.StorageServiceConfiguration;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractStorageConfiguration<T extends Enum<T>> implements StorageServiceConfiguration<T> {
    protected final Map<T, Object> properties;

    private final EnumSet<T> propertyEnumElements;

    protected AbstractStorageConfiguration() {
        this(new HashMap<>());
    }

    protected AbstractStorageConfiguration(Map<T, Object> properties) {
        propertyEnumElements = EnumSet.allOf(getPropertiesEnumClass());
        this.properties = properties;
    }

    protected abstract Map<T, Class<?>> getPropertyTypes();

    protected abstract Class<T> getPropertiesEnumClass();

    @Override
    public Object getProperty(T property) {
        Conditions.checkContains(Condition.ARGUMENT, properties.keySet(), property, "Property '%s' is not set".formatted(property));
        return properties.get(property);
    }

    @Override
    public Object setProperty(T property, Object value) {
        return properties.put(property, value);
    }

    @Override
    public Map<T, Class<?>> getProperties() {
        var properties = getPropertyTypes();

        Conditions.check(Condition.STATE,
          properties.size() == propertyEnumElements.size(),
          "Not all configuration properties are included in the map to return. Update getProperties() implementation.");

        return properties;
    }
}
