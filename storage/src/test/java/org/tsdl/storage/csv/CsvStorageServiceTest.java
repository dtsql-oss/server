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
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.tsdl.infrastructure.model.DataPoint;

class CsvStorageServiceTest {

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

  @ParameterizedTest
  @MethodSource("org.tsdl.storage.csv.stub.CsvRowDataFactory#threeEntries")
  void loadAndTransform_validInputs_returnsDataPoints(List<List<Object>> persistedData) throws IOException {
    var serviceConfig = new CsvStorageConfiguration();
    var lookupConfig = new CsvStorageConfiguration(Map.of(
        CsvStorageProperty.FILE_PATH, "D:\\Universitaet\\Diplomarbeit\\data\\clean\\annotated\\Analysis 1\\ABS_1_KEEA.csv",
        CsvStorageProperty.FIELD_SEPARATOR, ','
    ));
    var transformationConfig = new CsvStorageConfiguration(Map.of(
        CsvStorageProperty.VALUE_COLUMN, 1,
        CsvStorageProperty.SKIP_HEADERS, 0,
        CsvStorageProperty.TIME_COLUMN, 2,
        CsvStorageProperty.TIME_FORMAT, "MM/dd/yyyy HH:mm:ss"
    ));

    testLoadAndTransformTestSuccess(
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
  @MethodSource("org.tsdl.storage.csv.stub.CsvRowDataFactory#threeEntries")
  void loadAndTransform_missingFieldSeparator_throws(List<List<Object>> persistedData) throws IOException {
    var serviceConfig = new CsvStorageConfiguration();
    var lookupConfig = new CsvStorageConfiguration(Map.of(
        CsvStorageProperty.FILE_PATH, "D:\\Universitaet\\Diplomarbeit\\data\\clean\\annotated\\Analysis 1\\ABS_1_KEEA.csv"
    ));
    var transformationConfig = new CsvStorageConfiguration(Map.of(
        CsvStorageProperty.VALUE_COLUMN, 1,
        CsvStorageProperty.SKIP_HEADERS, 0,
        CsvStorageProperty.TIME_COLUMN, 2,
        CsvStorageProperty.TIME_FORMAT, "MM/dd/yyyy HH:mm:ss"
    ));

    testLoadAndTransformTestFailure(
        persistedData,
        serviceConfig,
        lookupConfig,
        transformationConfig,
        IllegalArgumentException.class);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.storage.csv.stub.CsvRowDataFactory#threeEntries")
  void loadAndTransform_invalidTimeFormat_throws(List<List<Object>> persistedData) throws IOException {
    var serviceConfig = new CsvStorageConfiguration();
    var lookupConfig = new CsvStorageConfiguration(Map.of(
        CsvStorageProperty.FILE_PATH, "D:\\Universitaet\\Diplomarbeit\\data\\clean\\annotated\\Analysis 1\\ABS_1_KEEA.csv",
        CsvStorageProperty.FIELD_SEPARATOR, ','
    ));
    var transformationConfig = new CsvStorageConfiguration(Map.of(
        CsvStorageProperty.VALUE_COLUMN, 1,
        CsvStorageProperty.SKIP_HEADERS, 0,
        CsvStorageProperty.TIME_COLUMN, 2,
        CsvStorageProperty.TIME_FORMAT, "MM/dd/yyyy HH:mm"
    ));

    testLoadAndTransformTestFailure(
        persistedData,
        serviceConfig,
        lookupConfig,
        transformationConfig,
        DateTimeParseException.class);
  }

  private void testLoadAndTransformTestSuccess(List<List<Object>> persistedData, CsvStorageConfiguration serviceConfig,
                                               CsvStorageConfiguration lookupConfig, CsvStorageConfiguration transformationConfig,
                                               List<DataPoint> expectedDataPoints, boolean ignoreTimestamp) throws IOException {
    var dataPoints = executeLoadTransformTest(persistedData, serviceConfig, lookupConfig, transformationConfig, ignoreTimestamp);
    assertResults(dataPoints, expectedDataPoints, ignoreTimestamp);
  }

  private void testLoadAndTransformTestFailure(List<List<Object>> persistedData, CsvStorageConfiguration serviceConfig,
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
}
