package org.tsdl.storage.csv;

import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import org.tsdl.infrastructure.api.StorageService;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

import java.io.IOException;
import java.nio.file.Path;

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

    }

    @Override
    public Iterable<CsvRow> load(CsvStorageConfiguration lookupConfiguration) throws IOException {
        Conditions.checkIsTrue(Condition.STATE, isInitialized(), "CSV service has not been initialized yet. Call initialize() beforehand.");
        Conditions.checkNotNull(Condition.ARGUMENT, lookupConfiguration, "The lookup configuration must not be null.");
        Conditions.checkIsTrue(Condition.ARGUMENT,
          lookupConfiguration.isPropertySet(CsvStorageProperty.FILE_PATH),
          "Query property is required to load data with the CSV storage service.");
        Conditions.checkIsTrue(Condition.ARGUMENT,
          lookupConfiguration.isPropertySet(CsvStorageProperty.FIELD_SEPARATOR),
          "Field separator property is required to load data with the CSV storage service.");

        var filePath = lookupConfiguration.getProperty(CsvStorageProperty.FILE_PATH, String.class);
        var fieldSeparator = lookupConfiguration.getProperty(CsvStorageProperty.FIELD_SEPARATOR, Character.class);
        try (var csvReader = CsvReader.builder().fieldSeparator(fieldSeparator).build(Path.of(filePath))) {
            return csvReader.stream().toList();
        }
    }

    @Override
    public void close() {

    }
}
