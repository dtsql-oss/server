package org.tsdl.storage.csv;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.anyChar;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.tsdl.infrastructure.model.DataPoint;

@Slf4j
class CsvStorageServiceTest {

  @ParameterizedTest
  @MethodSource("org.tsdl.storage.csv.stub.CsvStorageTestDataFactory#threeDataPoints")
  void store_validInput_createsCorrectFile(List<DataPoint> data) throws IOException {
    testStoreSuccess(data, new CsvStorageConfiguration(), new CsvStorageConfiguration(Map.of(
            CsvStorageProperty.FIELD_SEPARATOR, ';',
            CsvStorageProperty.TIME_FORMAT, "yyyy-MM-dd HH:mm:ss",
            CsvStorageProperty.APPEND, false,
            CsvStorageProperty.INCLUDE_HEADERS, true,
            CsvStorageProperty.TIME_COLUMN_LABEL, "time",
            CsvStorageProperty.VALUE_COLUMN_LABEL, "value"
        )),
        true,
        """
            time;value
            2018-02-20 09:25:04;8394.283846
            2019-12-02 16:27:19;-98347383.0
            2021-07-30 23:12:54;363.2
            """);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.storage.csv.stub.CsvStorageTestDataFactory#threeDataPoints")
  void store_excludeHeaders_createsCorrectFile(List<DataPoint> data) throws IOException {
    testStoreSuccess(data, new CsvStorageConfiguration(), new CsvStorageConfiguration(Map.of(
            CsvStorageProperty.FIELD_SEPARATOR, ';',
            CsvStorageProperty.TIME_FORMAT, "yyyy-MM-dd HH:mm:ss",
            CsvStorageProperty.APPEND, false,
            CsvStorageProperty.INCLUDE_HEADERS, false
        )),
        true,
        """
            2018-02-20 09:25:04;8394.283846
            2019-12-02 16:27:19;-98347383.0
            2021-07-30 23:12:54;363.2
            """);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.storage.csv.stub.CsvStorageTestDataFactory#threeDataPoints")
  void store_specialFieldSeparator_escapesCorrectly(List<DataPoint> data) throws IOException {
    testStoreSuccess(data, new CsvStorageConfiguration(), new CsvStorageConfiguration(Map.of(
            CsvStorageProperty.FIELD_SEPARATOR, ' ',
            CsvStorageProperty.TIME_FORMAT, "yyyy-MM-dd HH:mm:ss",
            CsvStorageProperty.APPEND, false,
            CsvStorageProperty.INCLUDE_HEADERS, false
        )),
        true,
        """
            "2018-02-20 09:25:04" 8394.283846
            "2019-12-02 16:27:19" -98347383.0
            "2021-07-30 23:12:54" 363.2
            """);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.storage.csv.stub.CsvStorageTestDataFactory#threeDataPoints")
  void store_writeTwiceWithAppendSecondTime_fileContainsEntireContent(List<DataPoint> data) throws IOException {
    var file = testStoreSuccess(data, new CsvStorageConfiguration(), new CsvStorageConfiguration(Map.of(
            CsvStorageProperty.FIELD_SEPARATOR, ';',
            CsvStorageProperty.TIME_FORMAT, "yyyy-MM-dd HH:mm:ss",
            CsvStorageProperty.APPEND, false,
            CsvStorageProperty.INCLUDE_HEADERS, true,
            CsvStorageProperty.TIME_COLUMN_LABEL, "time",
            CsvStorageProperty.VALUE_COLUMN_LABEL, "value"
        )),
        false,
        """
            time;value
            2018-02-20 09:25:04;8394.283846
            2019-12-02 16:27:19;-98347383.0
            2021-07-30 23:12:54;363.2
            """);

    testStoreSuccess(file, data, new CsvStorageConfiguration(), new CsvStorageConfiguration(Map.of(
            CsvStorageProperty.FIELD_SEPARATOR, ';',
            CsvStorageProperty.TIME_FORMAT, "yyyy-MM-dd HH:mm:ss",
            CsvStorageProperty.APPEND, true,
            CsvStorageProperty.INCLUDE_HEADERS, false
        )),
        true,
        """
            time;value
            2018-02-20 09:25:04;8394.283846
            2019-12-02 16:27:19;-98347383.0
            2021-07-30 23:12:54;363.2
            2018-02-20 09:25:04;8394.283846
            2019-12-02 16:27:19;-98347383.0
            2021-07-30 23:12:54;363.2
            """);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.storage.csv.stub.CsvStorageTestDataFactory#threeDataPoints")
  void store_appendWithoutExistingFile_succeeds(List<DataPoint> data) throws IOException {
    testStoreSuccess(data, new CsvStorageConfiguration(), new CsvStorageConfiguration(Map.of(
            CsvStorageProperty.FIELD_SEPARATOR, ';',
            CsvStorageProperty.TIME_FORMAT, "yyyy-MM-dd HH:mm:ss",
            CsvStorageProperty.APPEND, true,
            CsvStorageProperty.INCLUDE_HEADERS, true,
            CsvStorageProperty.TIME_COLUMN_LABEL, "time",
            CsvStorageProperty.VALUE_COLUMN_LABEL, "value"
        )),
        true,
        """
            time;value
            2018-02-20 09:25:04;8394.283846
            2019-12-02 16:27:19;-98347383.0
            2021-07-30 23:12:54;363.2
            """);
  }

  @Test
  void store_includeHeadersWithoutLabels_throws() {
    testStoreFailure(List.of(), new CsvStorageConfiguration(), new CsvStorageConfiguration(Map.of(
            CsvStorageProperty.FIELD_SEPARATOR, ';',
            CsvStorageProperty.TIME_FORMAT, "yyyy-MM-dd HH:mm:ss",
            CsvStorageProperty.APPEND, true,
            CsvStorageProperty.INCLUDE_HEADERS, true
        )),
        IllegalArgumentException.class);
  }

  @Test
  void store_invalidTimeFormat_throws() {
    testStoreFailure(List.of(), new CsvStorageConfiguration(), new CsvStorageConfiguration(Map.of(
        CsvStorageProperty.FILE_PATH, "./some/path",
        CsvStorageProperty.FIELD_SEPARATOR, ';',
            CsvStorageProperty.TIME_FORMAT, "invalid format",
            CsvStorageProperty.APPEND, true,
            CsvStorageProperty.INCLUDE_HEADERS, false
        )),
        IllegalArgumentException.class);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.storage.csv.stub.CsvStorageTestDataFactory#threeCsvRows")
  void loadAndTransform_validInputs_returnsDataPoints(List<List<Object>> persistedData) throws IOException {
    var serviceConfig = new CsvStorageConfiguration();
    var lookupConfig = new CsvStorageConfiguration(Map.of(
        CsvStorageProperty.FILE_PATH, "/some/path",
        CsvStorageProperty.FIELD_SEPARATOR, ','
    ));
    var transformationConfig = new CsvStorageConfiguration(Map.of(
        CsvStorageProperty.VALUE_COLUMN, 1,
        CsvStorageProperty.SKIP_HEADERS, 0,
        CsvStorageProperty.TIME_COLUMN, 2,
        CsvStorageProperty.TIME_FORMAT, "MM/dd/yyyy HH:mm:ss"
    ));

    testLoadAndTransformSuccess(
        persistedData,
        serviceConfig,
        lookupConfig,
        transformationConfig,
        List.of(
            DataPoint.of(Instant.now(), "value1"),
            DataPoint.of(Instant.now(), "value2"),
            DataPoint.of(Instant.now(), "value3")
        ),
        true);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.storage.csv.stub.CsvStorageTestDataFactory#threeCsvRows")
  void loadAndTransform_missingFieldSeparator_throws(List<List<Object>> persistedData) {
    var serviceConfig = new CsvStorageConfiguration();
    var lookupConfig = new CsvStorageConfiguration(Map.of(
        CsvStorageProperty.FILE_PATH, "C:\\some\\path"
    ));
    var transformationConfig = new CsvStorageConfiguration(Map.of(
        CsvStorageProperty.VALUE_COLUMN, 1,
        CsvStorageProperty.SKIP_HEADERS, 0,
        CsvStorageProperty.TIME_COLUMN, 2,
        CsvStorageProperty.TIME_FORMAT, "MM/dd/yyyy HH:mm:ss"
    ));

    testLoadAndTransformFailure(
        persistedData,
        serviceConfig,
        lookupConfig,
        transformationConfig,
        IllegalArgumentException.class);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.storage.csv.stub.CsvStorageTestDataFactory#threeCsvRows")
  void loadAndTransform_invalidTimeFormat_throws(List<List<Object>> persistedData) {
    var serviceConfig = new CsvStorageConfiguration();
    var lookupConfig = new CsvStorageConfiguration(Map.of(
        CsvStorageProperty.FILE_PATH, "/mnt/path/to/file",
        CsvStorageProperty.FIELD_SEPARATOR, ','
    ));
    var transformationConfig = new CsvStorageConfiguration(Map.of(
        CsvStorageProperty.VALUE_COLUMN, 1,
        CsvStorageProperty.SKIP_HEADERS, 0,
        CsvStorageProperty.TIME_COLUMN, 2,
        CsvStorageProperty.TIME_FORMAT, "MM/dd/yyyy HH:mm"
    ));

    testLoadAndTransformFailure(
        persistedData,
        serviceConfig,
        lookupConfig,
        transformationConfig,
        DateTimeParseException.class);
  }

  private void testStoreFailure(List<DataPoint> data, CsvStorageConfiguration serviceConfig, CsvStorageConfiguration persistConfig,
                                Class<? extends Throwable> expectedException) {
    var service = new CsvStorageService();
    service.initialize(serviceConfig);

    assertThatThrownBy(() -> service.store(data, persistConfig))
        .isInstanceOf(expectedException);
  }

  private Path testStoreSuccess(List<DataPoint> data, CsvStorageConfiguration serviceConfig, CsvStorageConfiguration persistConfig,
                                boolean delete, String expectedFileContents) throws IOException {
    var tempFilePath = Path.of(System.getProperty("java.io.tmpdir"), "CsvStorageTest_" + UUID.randomUUID() + ".csv");
    testStoreSuccess(tempFilePath, data, serviceConfig, persistConfig, delete, expectedFileContents);
    return tempFilePath;
  }

  private void testStoreSuccess(Path tempFilePath, List<DataPoint> data, CsvStorageConfiguration serviceConfig, CsvStorageConfiguration persistConfig,
                                boolean delete, String expectedFileContents) throws IOException {
    var tempFile = tempFilePath.toString();

    try {
      var service = new CsvStorageService();
      service.initialize(serviceConfig);

      persistConfig.setProperty(CsvStorageProperty.FILE_PATH, tempFile);
      service.store(data, persistConfig);

      var writtenFile = Files.readString(tempFilePath);
      var normalizedExpected = expectedFileContents.replaceAll("(\\r\\n|\\r|\\n)", System.lineSeparator());
      assertThat(writtenFile)
          .isEqualTo(normalizedExpected);
    } finally {
      if (delete) {
        try {
          Files.delete(tempFilePath);
        } catch (Exception e) {
          log.warn("Could not delete temp CSV file '{}': {}", tempFile, e.getMessage());
        }

      }
    }
  }

  private void testLoadAndTransformSuccess(List<List<Object>> persistedData, CsvStorageConfiguration serviceConfig,
                                           CsvStorageConfiguration lookupConfig, CsvStorageConfiguration transformationConfig,
                                           List<DataPoint> expectedDataPoints, boolean ignoreTimestamp) throws IOException {
    var dataPoints = executeLoadTransformTest(persistedData, serviceConfig, lookupConfig, transformationConfig, ignoreTimestamp);
    assertResults(dataPoints, expectedDataPoints, ignoreTimestamp);
  }

  private void testLoadAndTransformFailure(List<List<Object>> persistedData, CsvStorageConfiguration serviceConfig,
                                           CsvStorageConfiguration lookupConfig, CsvStorageConfiguration transformationConfig,
                                           Class<? extends Throwable> expectedException) {
    assertThatThrownBy(() -> executeLoadTransformTest(persistedData,
        serviceConfig,
        lookupConfig,
        transformationConfig,
        false)
    ).isInstanceOf(expectedException);
  }

  private List<DataPoint> executeLoadTransformTest(List<List<Object>> persistedData, CsvStorageConfiguration serviceConfig,
                                                   CsvStorageConfiguration lookupConfig, CsvStorageConfiguration transformationConfig,
                                                   boolean ignoreTimestamp) throws IOException {
    var service = getCsvStorageServiceSpy(persistedData);
    service.initialize(serviceConfig);

    var tables = service.load(lookupConfig);
    return service.transform(tables, transformationConfig);
  }

  private void assertResults(List<DataPoint> actual, List<DataPoint> expected, boolean ignoreTimestamp) {
    var fieldsToIgnore = ignoreTimestamp ? new String[] {"timestamp"} : new String[0];
    assertThat(actual)
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields(fieldsToIgnore)
        .hasSize(expected.size())
        .hasSameElementsAs(expected);
  }

  private static CsvStorageService getCsvStorageServiceSpy(List<List<Object>> persistedData) throws IOException {
    var service = spy(CsvStorageService.class);
    //noinspection resource
    doAnswer(invocationOnMock -> csvReaderMockFromData(persistedData))
        .when(service)
        .createReader(anyString(), anyChar());
    return service;
  }

  private static CsvReader csvReaderMockFromData(List<List<Object>> persistedData) {
    var mockRows = csvRowMocksFromData(persistedData);
    var reader = mock(CsvReader.class);
    when(reader.stream()).thenReturn(mockRows.stream());
    return reader;
  }

  private static List<CsvRow> csvRowMocksFromData(List<List<Object>> persistedData) {
    var mockRows = new ArrayList<CsvRow>();

    for (var row : persistedData) {
      var newRow = mock(CsvRow.class);

      when(newRow.getFields())
          .thenAnswer(i -> persistedData.stream().map(Object::toString).toList());

      when(newRow.getField(anyInt()))
          .thenAnswer(i -> {
            var index = i.getArgument(0, Integer.class);
            return row.get(index).toString();
          });
      mockRows.add(newRow);
    }

    return mockRows;
  }
}
