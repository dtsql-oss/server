package org.tsdl.storage.csv;

import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import org.jetbrains.annotations.NotNull;
import org.tsdl.infrastructure.api.StorageService;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class CsvStorageService implements StorageService<CsvRow, CsvStorageConfiguration> {
    @Override
    public void initialize(CsvStorageConfiguration serviceConfiguration) {
        // no initialization needed
    }

    @Override
    public boolean isInitialized() {
        return true;
    }

    @Override
    public void store(CsvStorageConfiguration persistConfiguration) {
        Conditions.checkIsTrue(Condition.STATE, isInitialized(), "InfluxDB service has not been initialized yet. Call initialize() beforehand.");
        Conditions.checkNotNull(Condition.ARGUMENT, persistConfiguration, "The persist configuration must not be null.");

        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<CsvRow> load(CsvStorageConfiguration lookupConfiguration) throws IOException {
        Conditions.checkIsTrue(Condition.STATE, isInitialized(), "CSV service has not been initialized yet. Call initialize() beforehand.");
        Conditions.checkNotNull(Condition.ARGUMENT, lookupConfiguration, "The lookup configuration must not be null.");
        Conditions.checkIsTrue(Condition.ARGUMENT,
          lookupConfiguration.isPropertySet(CsvStorageProperty.FILE_PATH),
          "'%s' property ('%s') is required to load data with the CSV storage service.",
          CsvStorageProperty.FILE_PATH.name(), CsvStorageProperty.FILE_PATH.identifier());
        Conditions.checkIsTrue(Condition.ARGUMENT,
          lookupConfiguration.isPropertySet(CsvStorageProperty.FIELD_SEPARATOR),
          "'%s' property ('%s') is required to load data with the CSV storage service.",
          CsvStorageProperty.FIELD_SEPARATOR.name(), CsvStorageProperty.FIELD_SEPARATOR.identifier());

        var filePath = lookupConfiguration.getProperty(CsvStorageProperty.FILE_PATH, String.class);
        var fieldSeparator = lookupConfiguration.getProperty(CsvStorageProperty.FIELD_SEPARATOR, Character.class);
        try (var csvReader = createReader(filePath, fieldSeparator)) {
            return csvReader.stream().toList();
        }
    }

    @Override
    public List<DataPoint> transform(List<CsvRow> loadedData, CsvStorageConfiguration transformationConfiguration) {
        Conditions.checkNotNull(Condition.ARGUMENT, transformationConfiguration, "The transformation configuration must not be null.");
        Conditions.checkNotNull(Condition.ARGUMENT, loadedData, "Data to transform must not be null.");
        Conditions.checkIsTrue(Condition.ARGUMENT,
          transformationConfiguration.isPropertySet(CsvStorageProperty.VALUE_COLUMN),
          "'%s' property ('%s') is required to transform data loaded by the CSV storage service into data points.",
          CsvStorageProperty.VALUE_COLUMN.name(), CsvStorageProperty.VALUE_COLUMN.identifier());
        Conditions.checkIsTrue(Condition.ARGUMENT,
          transformationConfiguration.isPropertySet(CsvStorageProperty.TIME_COLUMN),
          "'%s' property ('%s') is required to transform data loaded by the CSV storage service into data points.",
          CsvStorageProperty.TIME_COLUMN.name(), CsvStorageProperty.TIME_COLUMN.identifier());
        Conditions.checkIsTrue(Condition.ARGUMENT,
          transformationConfiguration.isPropertySet(CsvStorageProperty.TIME_FORMAT),
          "'%s' property ('%s') is required to transform data loaded by the CSV storage service into data points.",
          CsvStorageProperty.TIME_FORMAT.name(), CsvStorageProperty.TIME_FORMAT.identifier());
        Conditions.checkIsTrue(Condition.ARGUMENT,
          transformationConfiguration.isPropertySet(CsvStorageProperty.SKIP_HEADERS),
          "'%s' property ('%s') is required to transform data loaded by the CSV storage service into data points.",
          CsvStorageProperty.SKIP_HEADERS.name(), CsvStorageProperty.SKIP_HEADERS.identifier());

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
              Conditions.checkValidIndex(Condition.STATE, row.getFields(), timeIndex, "Time column index '%s' is not valid for row '%s'.", timeIndex, row.toString());
              Conditions.checkValidIndex(Condition.STATE, row.getFields(), valueIndex, "Value column index '%s' is not valid for row '%s'.", valueIndex, row.toString());

              var dateTime = row.getField(timeIndex);
              var value = row.getField(valueIndex);

              return DataPoint.of(Instant.from(formatter.parse(dateTime)), value);
          }).toList();
    }

    @Override
    public void close() {
        // nothing to do, all closable resources are closed upon usage
    }

    @NotNull
    CsvReader createReader(String filePath, Character fieldSeparator) throws IOException {
        return CsvReader.builder()
          .fieldSeparator(fieldSeparator)
          .build(Path.of(filePath));
    }
}
