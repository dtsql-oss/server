package org.tsdl.storage.csv;

import de.siegmar.fastcsv.reader.CsvRow;
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

        try (var service = new CsvStorageService()) {
            service.initialize(serviceConfig);
            var csvRows = service.load(lookupConfig);
            for (CsvRow csvRow : csvRows) {
                System.out.println(csvRow.toString());
            }
        }
    }
}
