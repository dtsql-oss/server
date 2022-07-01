package org.tsdl.service.mapper;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import org.mapstruct.Mapper;
import org.tsdl.infrastructure.api.StorageProperty;
import org.tsdl.infrastructure.api.StorageServiceConfiguration;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.service.exception.ServiceResolutionException;

@Mapper
public abstract class StorageServiceConfigurationMapper {
  // TODO instead of confusing, nested map, introduce PropertyValueConverter class and use a collection of them in here
  private static final Map<Class<?>, Map<Class<?>, UnaryOperator<Object>>> PROPERTY_VALUE_CONVERTERS = Map.of(
      Character.class, Map.of(
          String.class, v -> {
            var input = (String) v;
            Conditions.checkIsTrue(Condition.ARGUMENT, input.length() == 1, "String to map to character must be of length 1.");
            return input.charAt(0);
          }
      ),
      char[].class, Map.of(
          String.class, v -> ((String) v).toCharArray()
      ),
      Instant.class, Map.of(
          String.class, v -> Instant.from(DateTimeFormatter.ISO_INSTANT.parse((String) v))
      )
  );

  public StorageServiceConfiguration mapToConfiguration(Map<String, Object> properties,
                                                        Supplier<StorageServiceConfiguration> configurationSupplier,
                                                        Class<? extends Enum<?>> propertyClass) throws ServiceResolutionException {
    try {
      var serviceConfiguration = configurationSupplier.get();

      if (properties == null) {
        return serviceConfiguration;
      }

      for (var mapping : properties.entrySet()) {
        var property = StorageProperty.fromIdentifier(mapping.getKey(), propertyClass);
        var typeConformValue = retrieveValue(property, mapping.getValue());
        serviceConfiguration.setProperty(property, typeConformValue);
      }
      return serviceConfiguration;
    } catch (Exception e) {
      throw new ServiceResolutionException("Could not map storage configuration.", e);
    }
  }

  private Object retrieveValue(StorageProperty property, Object value) {
    Conditions.checkNotNull(Condition.ARGUMENT, value, "Configuration property value ('%s') must not be null.", property.identifier());
    var targetType = property.type();
    if (targetType.isAssignableFrom(value.getClass())) {
      return value;
    } else if (PROPERTY_VALUE_CONVERTERS.containsKey(targetType)) {
      var targetTypeConverters = PROPERTY_VALUE_CONVERTERS.get(targetType);
      if (targetTypeConverters.containsKey(value.getClass())) {
        var valueConverter = targetTypeConverters.get(value.getClass());
        return valueConverter.apply(value);
      }
    }
    return value; // if we reach this line, we will most likely run into an IllegalArgumentException ("invalid type for property value") later on
  }
}
