package org.tsdl.storage.csv;

import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import de.siegmar.fastcsv.writer.CsvWriter;
import de.siegmar.fastcsv.writer.LineDelimiter;
import de.siegmar.fastcsv.writer.QuoteStrategy;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.tsdl.infrastructure.api.StorageService;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.storage.BaseStorageService;

/**
 * An implementation of {@link StorageService} for a storage mechanism targeting CSV files.
 */
public final class CsvStorageService extends BaseStorageService implements StorageService<CsvRow, CsvStorageConfiguration> {
  public static final String STORE_PROPERTY_REQUIRED = "'%s' property ('%s') is required to store data with the CSV storage service.";
  public static final String LOAD_PROPERTY_REQUIRED = "'%s' property ('%s') is required to load data with the CSV storage service.";
  public static final String TRANSFORMATION_PROPERTY_REQUIRED =
      "'%s' property ('%s') is required to transform data loaded by the CSV storage service into data points.";

  @Override
  public void initialize(CsvStorageConfiguration serviceConfiguration) {
    // no initialization needed
  }

  @Override
  public boolean isInitialized() {
    return true;
  }

  @Override
  public void store(List<DataPoint> data, CsvStorageConfiguration persistConfiguration) {
    safeStorageAccess(() -> {
      Conditions.checkIsTrue(Condition.STATE, isInitialized(), "InfluxDB service has not been initialized yet. Call initialize() beforehand.");
      Conditions.checkNotNull(Condition.ARGUMENT, persistConfiguration, "The persist configuration must not be null.");
      requireProperty(persistConfiguration, CsvStorageProperty.FILE_PATH, STORE_PROPERTY_REQUIRED);
      requireProperty(persistConfiguration, CsvStorageProperty.FIELD_SEPARATOR, STORE_PROPERTY_REQUIRED);
      requireProperty(persistConfiguration, CsvStorageProperty.TIME_FORMAT, STORE_PROPERTY_REQUIRED);
      requireProperty(persistConfiguration, CsvStorageProperty.APPEND, STORE_PROPERTY_REQUIRED);
      requireProperty(persistConfiguration, CsvStorageProperty.INCLUDE_HEADERS, STORE_PROPERTY_REQUIRED);

      var includeHeaders = persistConfiguration.getProperty(CsvStorageProperty.INCLUDE_HEADERS, Boolean.class);
      if (Boolean.TRUE.equals(includeHeaders)) {
        requireProperty(persistConfiguration, CsvStorageProperty.TIME_COLUMN_LABEL,
            "Since headers are included, '%s' property ('%s') is required to transform data loaded by the CSV storage service into data points.");
        requireProperty(persistConfiguration, CsvStorageProperty.VALUE_COLUMN_LABEL,
            "Since headers are included, '%s' property ('%s') is required to transform data loaded by the CSV storage service into data points.");
      }

      var formatter = DateTimeFormatter
          .ofPattern(persistConfiguration.getProperty(CsvStorageProperty.TIME_FORMAT, String.class))
          .withZone(ZoneOffset.UTC);

      var filePath = persistConfiguration.getProperty(CsvStorageProperty.FILE_PATH, String.class);
      var fieldSeparator = persistConfiguration.getProperty(CsvStorageProperty.FIELD_SEPARATOR, Character.class);
      var append = persistConfiguration.getProperty(CsvStorageProperty.APPEND, Boolean.class);

      try (var csvWriter = createWriter(filePath, fieldSeparator, append)) {
        if (Boolean.TRUE.equals(includeHeaders)) {
          csvWriter.writeRow(
              persistConfiguration.getProperty(CsvStorageProperty.TIME_COLUMN_LABEL, String.class),
              persistConfiguration.getProperty(CsvStorageProperty.VALUE_COLUMN_LABEL, String.class)
          );
        }

        for (DataPoint dp : data) {
          var timeString = formatter.format(dp.timestamp());
          csvWriter.writeRow(timeString, dp.asText());
        }
      }
    });
  }

  @Override
  public List<CsvRow> load(CsvStorageConfiguration lookupConfiguration) {
    return safeStorageAccess(() -> {
      Conditions.checkIsTrue(Condition.STATE, isInitialized(), "CSV service has not been initialized yet. Call initialize() beforehand.");
      Conditions.checkNotNull(Condition.ARGUMENT, lookupConfiguration, "The lookup configuration must not be null.");
      requireProperty(lookupConfiguration, CsvStorageProperty.FILE_PATH, LOAD_PROPERTY_REQUIRED);
      requireProperty(lookupConfiguration, CsvStorageProperty.FIELD_SEPARATOR, LOAD_PROPERTY_REQUIRED);

      var filePath = lookupConfiguration.getProperty(CsvStorageProperty.FILE_PATH, String.class);
      var fieldSeparator = lookupConfiguration.getProperty(CsvStorageProperty.FIELD_SEPARATOR, Character.class);
      try (var csvReader = createReader(filePath, fieldSeparator)) {
        return csvReader.stream().toList();
      }
    });
  }

  @Override
  public List<DataPoint> transform(List<CsvRow> loadedData, CsvStorageConfiguration transformationConfiguration) {
    return safeStorageAccess(() -> {
      Conditions.checkNotNull(Condition.ARGUMENT, transformationConfiguration, "The transformation configuration must not be null.");
      Conditions.checkNotNull(Condition.ARGUMENT, loadedData, "Data to transform must not be null.");
      requireProperty(transformationConfiguration, CsvStorageProperty.VALUE_COLUMN, TRANSFORMATION_PROPERTY_REQUIRED);
      requireProperty(transformationConfiguration, CsvStorageProperty.TIME_COLUMN, TRANSFORMATION_PROPERTY_REQUIRED);
      requireProperty(transformationConfiguration, CsvStorageProperty.TIME_FORMAT, TRANSFORMATION_PROPERTY_REQUIRED);
      requireProperty(transformationConfiguration, CsvStorageProperty.SKIP_HEADERS, TRANSFORMATION_PROPERTY_REQUIRED);

      var skipHeaders = transformationConfiguration.getProperty(CsvStorageProperty.SKIP_HEADERS, Integer.class);
      Conditions.checkIsGreaterThanOrEqual(Condition.ARGUMENT,
          skipHeaders,
          0,
          "'%' property ('%s') must be greater than or equal to 0.",
          CsvStorageProperty.SKIP_HEADERS.name(), CsvStorageProperty.SKIP_HEADERS.identifier());

      var valueIndex = transformationConfiguration.getProperty(CsvStorageProperty.VALUE_COLUMN, Integer.class);
      var timeIndex = transformationConfiguration.getProperty(CsvStorageProperty.TIME_COLUMN, Integer.class);
      var formatter = DateTimeFormatter
          .ofPattern(transformationConfiguration.getProperty(CsvStorageProperty.TIME_FORMAT, String.class))
          .withZone(ZoneOffset.UTC);

      return loadedData.stream()
          .skip(skipHeaders)
          .map(row -> {
            Conditions.checkValidIndex(Condition.STATE, row.getFields(), timeIndex, "Time column index '%s' is not valid for row '%s'.", timeIndex,
                row.toString());
            Conditions.checkValidIndex(Condition.STATE, row.getFields(), valueIndex, "Value column index '%s' is not valid for row '%s'.", valueIndex,
                row.toString());

            var dateTime = row.getField(timeIndex);
            var value = row.getField(valueIndex);

            return DataPoint.of(Instant.from(formatter.parse(dateTime)), Double.valueOf(value));
          }).toList();
    });
  }

  @Override
  public void close() {
    // nothing to do, all closable resources are closed upon usage
  }

  @NotNull
  CsvReader createReader(String filePath, Character fieldSeparator) throws IOException {
    return CsvReader.builder()
        .fieldSeparator(fieldSeparator)
        .build(Path.of(filePath), StandardCharsets.UTF_8);
  }

  @NotNull
  CsvWriter createWriter(String filePath, Character fieldSeparator, boolean append) throws IOException {
    var options = append
        ? new OpenOption[] {StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND}
        : new OpenOption[] {StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING};
    return CsvWriter.builder()
        .fieldSeparator(fieldSeparator)
        .quoteCharacter('"')
        .quoteStrategy(QuoteStrategy.REQUIRED)
        .lineDelimiter(LineDelimiter.PLATFORM)
        .build(Path.of(filePath), StandardCharsets.UTF_8, options);
  }

  private void requireProperty(CsvStorageConfiguration config, CsvStorageProperty property, String messageTemplate) {
    Conditions.checkIsTrue(Condition.ARGUMENT,
        config.isPropertySet(property),
        messageTemplate,
        property.name(), property.identifier());
  }
}
