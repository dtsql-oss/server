package org.tsdl.infrastructure.api;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

/**
 * An abstract base class for preferred implementations of the {@link StorageServiceConfiguration} interface, namely as enums.
 */
public abstract class EnumStorageConfiguration implements StorageServiceConfiguration {
  public static final String PROPERTY_MUST_NOT_BE_NULL = "Property must not be null.";
  protected final Map<StorageProperty, Object> properties;

  protected EnumStorageConfiguration() {
    this(new HashMap<>());
  }

  protected EnumStorageConfiguration(Map<StorageProperty, Object> properties) {
    this.properties = new HashMap<>();
    properties.forEach(this::setProperty);
  }

  @Override
  public boolean isPropertySet(StorageProperty property) {
    Conditions.checkNotNull(Condition.ARGUMENT, property, PROPERTY_MUST_NOT_BE_NULL);
    checkKnownProperty(property);

    return properties.containsKey(property);
  }

  @Override
  public Object getProperty(StorageProperty property) {
    Conditions.checkNotNull(Condition.ARGUMENT, property, PROPERTY_MUST_NOT_BE_NULL);
    checkKnownProperty(property);

    return properties.get(property);
  }

  @Override
  public <V> V getProperty(StorageProperty property, Class<V> targetType) {
    Conditions.checkNotNull(Condition.ARGUMENT, property, PROPERTY_MUST_NOT_BE_NULL);
    Conditions.checkNotNull(Condition.ARGUMENT, targetType, "Target type must not be null.");
    checkKnownProperty(property);

    Conditions.checkIsTrue(Condition.ARGUMENT,
        property.type().isAssignableFrom(targetType),
        "Type of property '%s' ('%s') cannot be cast to '%s'.",
        property,
        property.type().getTypeName(),
        targetType.getTypeName());

    return targetType.cast(getProperty(property));
  }

  @Override
  public Object setProperty(StorageProperty property, Object value) {
    Conditions.checkNotNull(Condition.ARGUMENT, property, PROPERTY_MUST_NOT_BE_NULL);
    Conditions.checkNotNull(Condition.ARGUMENT, value, "Property value must not be null.");
    checkKnownProperty(property);

    var valueHasCorrectType = property.type().isAssignableFrom(value.getClass());
    Conditions.checkIsTrue(Condition.ARGUMENT,
        valueHasCorrectType,
        "'%s' is not a valid type for property '%s' ('%s').",
        value.getClass().getTypeName(),
        property,
        property.type().getTypeName());

    return properties.put(property, value);
  }

  @Override
  public Object unsetProperty(StorageProperty property) {
    Conditions.checkNotNull(Condition.ARGUMENT, property, PROPERTY_MUST_NOT_BE_NULL);
    checkKnownProperty(property);

    return properties.remove(property);
  }

  @Override
  public Map<StorageProperty, Object> getSetProperties() {
    return getSupportedProperties().stream()
        .filter(this::isPropertySet)
        .collect(Collectors.toMap(
            Function.identity(), this::getProperty)
        );
  }

  private void checkKnownProperty(StorageProperty property) {
    Conditions.checkContains(Condition.ARGUMENT, getSupportedProperties(), property,
        "Property '%s' ('%s') is not supported by this StorageConfiguration.", property, property.getClass().getTypeName());
  }
}
