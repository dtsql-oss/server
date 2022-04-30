package org.tsdl.storage.csv;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

public class CsvStorageServiceTest {
    // TODO mock/stub

    @Test
    void load() throws IOException {
        var serviceConfig = new CsvStorageConfiguration();
        var lookupConfig = new CsvStorageConfiguration(Map.of(
          CsvStorageProperty.FILE_PATH, "D:\\Universitaet\\Diplomarbeit\\data\\clean\\annotated\\Analysis 1\\ABS_1_KEEA.csv",
          CsvStorageProperty.FIELD_SEPARATOR, ','
        ));
        var transformationConfig = new CsvStorageConfiguration(Map.of(
          CsvStorageProperty.VALUE_COLUMN, 1,
          CsvStorageProperty.SKIP_HEADERS, 4,
          CsvStorageProperty.TIME_COLUMN, 0,
          CsvStorageProperty.TIME_FORMAT, "MM/dd/yyyy HH:mm:ss"
        ));

        try (var service = new CsvStorageService()) {
            service.initialize(serviceConfig);
            var csvRows = service.load(lookupConfig);
            var dataPoints = service.transform(csvRows,transformationConfig);
            dataPoints.forEach(pt -> System.out.println(pt.toString()));
        }
    }
}
