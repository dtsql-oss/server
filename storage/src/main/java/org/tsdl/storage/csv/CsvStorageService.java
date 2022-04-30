package org.tsdl.storage.csv;

import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import org.tsdl.infrastructure.api.StorageService;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CsvStorageService implements StorageService<CsvRow, CsvStorageConfiguration, CsvStorageProperty> {
    @Override
    public void initialize(CsvStorageConfiguration serviceConfiguration) {
    }

    @Override
    public boolean isInitialized() {
        return true;
    }

    @Override
    public void store(CsvStorageConfiguration storageConfiguration) {
        Conditions.checkIsTrue(Condition.STATE, isInitialized(), "InfluxDB service has not been initialized yet. Call initialize() beforehand.");
        Conditions.checkNotNull(Condition.ARGUMENT, storageConfiguration, "The storage configuration must not be null.");

        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<CsvRow> load(CsvStorageConfiguration lookupConfiguration) throws IOException {
        Conditions.checkIsTrue(Condition.STATE, isInitialized(), "CSV service has not been initialized yet. Call initialize() beforehand.");
        Conditions.checkNotNull(Condition.ARGUMENT, lookupConfiguration, "The lookup configuration must not be null.");
        Conditions.checkIsTrue(Condition.ARGUMENT,
          lookupConfiguration.isPropertySet(CsvStorageProperty.FILE_PATH),
          "'FILE_PATH' property is required to load data with the CSV storage service.");
        Conditions.checkIsTrue(Condition.ARGUMENT,
          lookupConfiguration.isPropertySet(CsvStorageProperty.FIELD_SEPARATOR),
          "'FILE_SEPARATOR' property is required to load data with the CSV storage service.");

        var filePath = lookupConfiguration.getProperty(CsvStorageProperty.FILE_PATH, String.class);
        var fieldSeparator = lookupConfiguration.getProperty(CsvStorageProperty.FIELD_SEPARATOR, Character.class);
        try (var csvReader = CsvReader.builder().fieldSeparator(fieldSeparator).build(Path.of(filePath))) {
            return csvReader.stream().toList();
        }
    }

    @Override
    public List<DataPoint> transform(List<CsvRow> loadedData, CsvStorageConfiguration transformationConfiguration) {
        Conditions.checkNotNull(Condition.ARGUMENT, transformationConfiguration, "The transformation configuration must not be null.");
        Conditions.checkNotNull(Condition.ARGUMENT, loadedData, "Data to transform must not be null.");
        Conditions.checkIsTrue(Condition.ARGUMENT,
          transformationConfiguration.isPropertySet(CsvStorageProperty.VALUE_COLUMN),
          "'COLUMN_INDEX' property is required to transform data loaded by the CSV storage service into data points.");
        Conditions.checkIsTrue(Condition.ARGUMENT,
          transformationConfiguration.isPropertySet(CsvStorageProperty.TIME_COLUMN),
          "'TIME_COLUMN' property is required to transform data loaded by the CSV storage service into data points.");
        Conditions.checkIsTrue(Condition.ARGUMENT,
          transformationConfiguration.isPropertySet(CsvStorageProperty.TIME_FORMAT),
          "'TIME_FORMAT' property is required to transform data loaded by the CSV storage service into data points.");
        Conditions.checkIsTrue(Condition.ARGUMENT,
          transformationConfiguration.isPropertySet(CsvStorageProperty.SKIP_HEADERS),
          "'SKIP_HEADERS' property is required to transform data loaded by the CSV storage service into data points.");


        var skipHeaders = transformationConfiguration.getProperty(CsvStorageProperty.SKIP_HEADERS, Integer.class);
        Conditions.checkIsGreaterThanOrEqual(Condition.ARGUMENT,
          skipHeaders,
          0,
          "'SKIP_HEADERS' property must be greater than or equal to 0.");

        var valueIndex = transformationConfiguration.getProperty(CsvStorageProperty.VALUE_COLUMN, Integer.class);
        var timeIndex = transformationConfiguration.getProperty(CsvStorageProperty.TIME_COLUMN, Integer.class);
        var formatter = DateTimeFormatter
          .ofPattern(transformationConfiguration.getProperty(CsvStorageProperty.TIME_FORMAT, String.class))
          .withZone(ZoneId.systemDefault());

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

    }
}
