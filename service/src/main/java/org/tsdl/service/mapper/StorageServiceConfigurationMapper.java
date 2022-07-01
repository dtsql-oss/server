package org.tsdl.service.mapper;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Supplier;
import org.mapstruct.Mapper;
import org.tsdl.infrastructure.api.StorageProperty;
import org.tsdl.infrastructure.api.StorageServiceConfiguration;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.service.exception.ServiceResolutionException;
import org.tsdl.service.mapper.converters.CharacterArrayValueConverter;
import org.tsdl.service.mapper.converters.CharacterValueConverter;
import org.tsdl.service.mapper.converters.InstantValueConverter;
import org.tsdl.service.mapper.converters.PropertyValueConverter;

@Mapper
public abstract class StorageServiceConfigurationMapper {
  private static final List<PropertyValueConverter<?>> AVAILABLE_CONVERTERS = List.of(
      new CharacterValueConverter(), new CharacterArrayValueConverter(), new InstantValueConverter()
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
    }

    var converter = AVAILABLE_CONVERTERS.stream()
        .filter(c -> c.canConvert(value, targetType))
        .findFirst()
        .orElseThrow(() -> new NoSuchElementException(
            String.format("There is no element converter available supporting a conversion from type '%s' to type '%s'.", value.getClass().getName(),
                targetType.getName())));

    return converter.convert(value);
  }
}
