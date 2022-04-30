package org.tsdl.service.mapper;

import org.mapstruct.Mapper;
import org.tsdl.infrastructure.api.StorageServiceConfiguration;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.service.exception.InputInterpretationException;
import org.tsdl.storage.csv.CsvStorageConfiguration;
import org.tsdl.storage.csv.CsvStorageProperty;
import org.tsdl.storage.influxdb.InfluxDbStorageConfiguration;
import org.tsdl.storage.influxdb.InfluxDbStorageProperty;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

@Mapper
public abstract class StorageServiceConfigurationMapper {
    private final Map<Class<?>, Map<Class<?>, Function<Object, Object>>> PROPERTY_VALUE_CONVERTERS = Map.of(
      Character.class, Map.of(
        String.class, (v) -> ((String) v).charAt(0) // TODO make more strict (i.e. throw instead of cutting of extraneous characters)?
      ),
      char[].class, Map.of(
        String.class, (v) -> ((String) v).toCharArray()
      ),
      Instant.class, Map.of(
        String.class, (v) -> Instant.from(DateTimeFormatter.ISO_INSTANT.parse(((String) v)))
      )
    );

    public InfluxDbStorageConfiguration mapToInfluxDbConfiguration(Map<String, Object> configuration) throws InputInterpretationException {
        var config = new InfluxDbStorageConfiguration();

        for (Map.Entry<String, Object> mapping : configuration.entrySet()) {
            InfluxDbStorageProperty property;
            try {
                property = InfluxDbStorageProperty.fromIdentifier(mapping.getKey());
            } catch (NoSuchElementException e) {
                throw new InputInterpretationException("There is no InfluxDbStorageProperty corresponding to '%s'".formatted(mapping.getKey()), e);
            }

            var typeConformValue = retrieveValue(config, property, mapping.getValue());
            config.setProperty(property, typeConformValue);
        }

        return config;
    }

    public CsvStorageConfiguration mapToCsvConfiguration(Map<String, Object> configuration) throws InputInterpretationException {
        var config = new CsvStorageConfiguration();

        for (Map.Entry<String, Object> mapping : configuration.entrySet()) {
            CsvStorageProperty property;
            try {
                property = CsvStorageProperty.fromIdentifier(mapping.getKey());
            } catch (NoSuchElementException e) {
                throw new InputInterpretationException("There is no CsvStorageProperty corresponding to '%s'".formatted(mapping.getKey()), e);
            }

            var typeConformValue = retrieveValue(config, property, mapping.getValue());
            config.setProperty(property, typeConformValue);
        }

        return config;
    }

    private <T extends Enum<T>> Object retrieveValue(StorageServiceConfiguration<?> config, T property, Object value) {
        Conditions.checkNotNull(Condition.ARGUMENT, value, "Configuration property value must not be null.");
        var targetType = config.getSupportedProperties().get(property);
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
