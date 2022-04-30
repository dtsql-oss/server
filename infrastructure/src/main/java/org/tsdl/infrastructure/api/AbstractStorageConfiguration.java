package org.tsdl.infrastructure.api;

import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractStorageConfiguration<T extends Enum<T>> implements StorageServiceConfiguration<T> {
    protected final Map<T, Object> properties;

    private final EnumSet<T> propertyEnumElements;

    protected AbstractStorageConfiguration() {
        this(new HashMap<>());
    }

    protected AbstractStorageConfiguration(Map<T, Object> properties) {
        propertyEnumElements = EnumSet.allOf(getPropertiesEnumClass());
        ensurePropertyTypesComplete();

        this.properties = new HashMap<>();
        properties.forEach(this::setProperty);
    }

    protected abstract Class<T> getPropertiesEnumClass();

    @Override
    public boolean isPropertySet(T property) {
        Conditions.checkNotNull(Condition.ARGUMENT, property, "Property must not be null.");

        return properties.containsKey(property);
    }

    @Override
    public Object getProperty(T property) {
        Conditions.checkNotNull(Condition.ARGUMENT, property, "Property must not be null.");

        return properties.get(property);
    }

    @Override
    public <V> V getProperty(T property, Class<V> targetType) {
        Conditions.checkNotNull(Condition.ARGUMENT, property, "Property must not be null.");
        Conditions.checkNotNull(Condition.ARGUMENT, targetType, "Target type must not be null.");

        var propertyType = getPropertyType(property);
        var castPossible = propertyType.isAssignableFrom(targetType);
        Conditions.checkIsTrue(Condition.ARGUMENT,
          castPossible,
          "Type of property '%s' ('%s') cannot be cast to '%s'.",
          property,
          propertyType.getTypeName(),
          targetType.getTypeName());

        return targetType.cast(getProperty(property));
    }

    @Override
    public Object setProperty(T property, Object value) {
        Conditions.checkNotNull(Condition.ARGUMENT, property, "Property must not be null.");
        Conditions.checkNotNull(Condition.ARGUMENT, value, "Property value must not be null.");

        var propertyType = getPropertyType(property);
        var valueHasCorrectType = propertyType.isAssignableFrom(value.getClass());
        Conditions.checkIsTrue(Condition.ARGUMENT,
          valueHasCorrectType,
          "'%s' is not a valid type for property '%s' ('%s').",
          value.getClass().getTypeName(),
          property,
          propertyType.getTypeName());

        return properties.put(property, value);
    }

    @Override
    public Object unsetProperty(T property) {
        Conditions.checkNotNull(Condition.ARGUMENT, property, "Property must not be null.");
        return properties.remove(property);
    }

    @Override
    public Map<T, Object> getSetProperties() {
        return getSupportedProperties().keySet().stream()
          .filter(this::isPropertySet)
          .collect(Collectors.toMap(
            Function.identity(), this::getProperty)
          );
    }

    private void ensurePropertyTypesComplete() {
        Conditions.checkEquals(Condition.STATE,
          getSupportedProperties().size(),
          propertyEnumElements.size(),
          "Not all configuration properties are included in the map to return. Update getSupportedProperties() implementation.");
    }

    private Class<?> getPropertyType(T property) {
        Conditions.checkNotNull(Condition.ARGUMENT, property, "Property must not be null.");

        return getSupportedProperties().get(property);
    }
}
