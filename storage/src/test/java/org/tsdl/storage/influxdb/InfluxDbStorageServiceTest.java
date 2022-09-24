package org.tsdl.storage.influxdb;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.storage.TsdlStorageException;

class InfluxDbStorageServiceTest {

  private static InfluxDbStorageService getInfluxDbStorageServiceSpy(List<List<List<Object>>> persistedData) {
    var service = spy(InfluxDbStorageService.class);

    doAnswer(invocationOnMock -> {
      var clientMock = mock(InfluxDBClient.class);
      var queryMock = mock(QueryApi.class);
      var mockedData = fluxTableMocksFromData(persistedData);
      when(clientMock.getQueryApi()).thenReturn(queryMock);
      when(queryMock.query(anyString())).thenReturn(mockedData);

      var serviceMock = (InfluxDbStorageService) invocationOnMock.getMock();
      service.dbClient = clientMock;
      serviceMock.queryApi = queryMock;
      return null;
    }).when(service)
        .initializeInternal(any(InfluxDbStorageConfiguration.class));

    return service;
  }

  private static List<FluxTable> fluxTableMocksFromData(List<List<List<Object>>> persistedData) {
    var mockTables = new ArrayList<FluxTable>();

    for (var table : persistedData) {
      var newTable = mock(FluxTable.class);

      var records = new ArrayList<FluxRecord>();
      for (var record : table) {
        var newRecord = mock(FluxRecord.class);
        when(newRecord.getTime()).thenReturn(Instant.from(DateTimeFormatter.ISO_INSTANT.parse(record.get(0).toString())));
        when(newRecord.getValue()).thenReturn(record.get(1));
        records.add(newRecord);
      }

      when(newTable.getRecords()).thenReturn(records);
      mockTables.add(newTable);
    }

    return mockTables;
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.storage.influxdb.stub.FluxTableDataFactory#threeSingletonTables")
  void loadAndTransform_nonNegativeTableIndex_returnsCorrespondingTable(List<List<List<Object>>> persistedData) {
    var serviceConfig = new InfluxDbStorageConfiguration(Map.of(
        InfluxDbStorageProperty.URL, "<url>",
        InfluxDbStorageProperty.ORGANIZATION, "<org>",
        InfluxDbStorageProperty.TOKEN, "<token>".toCharArray()
    ));
    var lookupConfig = new InfluxDbStorageConfiguration(Map.of(
        InfluxDbStorageProperty.QUERY, "query is irrelevant because data is mocked"
    ));
    var transformationConfig = new InfluxDbStorageConfiguration(Map.of(
        InfluxDbStorageProperty.TABLE_INDEX, 1
    ));

    testLoadAndTransformSuccess(persistedData,
        serviceConfig,
        lookupConfig,
        transformationConfig,
        List.of(
            DataPoint.of(Instant.now(), 24.0)
        ),
        true);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.storage.influxdb.stub.FluxTableDataFactory#threeSingletonTables")
  void loadAndTransform_minusOneTableIndex_returnsAllTables(List<List<List<Object>>> persistedData) {
    var serviceConfig = new InfluxDbStorageConfiguration(Map.of(
        InfluxDbStorageProperty.URL, "<url>",
        InfluxDbStorageProperty.ORGANIZATION, "<org>",
        InfluxDbStorageProperty.TOKEN, "<token>".toCharArray()
    ));
    var lookupConfig = new InfluxDbStorageConfiguration(Map.of(
        InfluxDbStorageProperty.QUERY, "query is irrelevant because data is mocked"
    ));
    var transformationConfig = new InfluxDbStorageConfiguration(Map.of(
        InfluxDbStorageProperty.TABLE_INDEX, -1
    ));

    testLoadAndTransformSuccess(persistedData,
        serviceConfig,
        lookupConfig,
        transformationConfig,
        List.of(
            DataPoint.of(Instant.now(), 23.0),
            DataPoint.of(Instant.now(), 24.0),
            DataPoint.of(Instant.now(), 25.0)
        ),
        true);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.storage.influxdb.stub.FluxTableDataFactory#threeSingletonTables")
  void loadAndTransform_tableIndexOutOfRange_throws(List<List<List<Object>>> persistedData) {
    var serviceConfig = new InfluxDbStorageConfiguration(Map.of(
        InfluxDbStorageProperty.URL, "<url>",
        InfluxDbStorageProperty.ORGANIZATION, "<org>",
        InfluxDbStorageProperty.TOKEN, "<token>".toCharArray()
    ));
    var lookupConfig = new InfluxDbStorageConfiguration(Map.of(
        InfluxDbStorageProperty.QUERY, "query is irrelevant because data is mocked"
    ));
    var transformationConfig = new InfluxDbStorageConfiguration(Map.of(
        InfluxDbStorageProperty.TABLE_INDEX, 24
    ));

    testLoadAndTransformFailure(persistedData,
        serviceConfig,
        lookupConfig,
        transformationConfig,
        TsdlStorageException.class,
        IllegalArgumentException.class);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.storage.influxdb.stub.FluxTableDataFactory#threeSingletonTables")
  void loadAndTransform_bothQueryAndBucketDatePropertiesGiven_throws(List<List<List<Object>>> persistedData) {
    var serviceConfig = new InfluxDbStorageConfiguration(Map.of(
        InfluxDbStorageProperty.URL, "<url>",
        InfluxDbStorageProperty.ORGANIZATION, "<org>",
        InfluxDbStorageProperty.TOKEN, "<token>".toCharArray()
    ));
    var lookupConfig = new InfluxDbStorageConfiguration(Map.of(
        InfluxDbStorageProperty.QUERY, "query is irrelevant because data is mocked",
        InfluxDbStorageProperty.LOAD_FROM, Instant.now(),
        InfluxDbStorageProperty.LOAD_UNTIL, Instant.now(),
        InfluxDbStorageProperty.BUCKET, "bucket3"
    ));
    var transformationConfig = new InfluxDbStorageConfiguration(Map.of(
        InfluxDbStorageProperty.TABLE_INDEX, 24
    ));

    testLoadAndTransformFailure(persistedData,
        serviceConfig,
        lookupConfig,
        transformationConfig,
        TsdlStorageException.class,
        IllegalArgumentException.class);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.storage.influxdb.stub.FluxTableDataFactory#threeSingletonTables")
  void loadAndTransform_missingTableIndex_throws(List<List<List<Object>>> persistedData) {
    var serviceConfig = new InfluxDbStorageConfiguration(Map.of(
        InfluxDbStorageProperty.URL, "<url>",
        InfluxDbStorageProperty.ORGANIZATION, "<org>",
        InfluxDbStorageProperty.TOKEN, "<token>".toCharArray()
    ));
    var lookupConfig = new InfluxDbStorageConfiguration(Map.of(
        InfluxDbStorageProperty.LOAD_FROM, Instant.now(),
        InfluxDbStorageProperty.LOAD_UNTIL, Instant.now(),
        InfluxDbStorageProperty.BUCKET, "bucket3"
    ));
    var transformationConfig = new InfluxDbStorageConfiguration(Map.of());

    testLoadAndTransformFailure(persistedData,
        serviceConfig,
        lookupConfig,
        transformationConfig,
        TsdlStorageException.class,
        IllegalArgumentException.class);
  }

  private void testLoadAndTransformSuccess(List<List<List<Object>>> persistedData, InfluxDbStorageConfiguration serviceConfig,
                                           InfluxDbStorageConfiguration lookupConfig, InfluxDbStorageConfiguration transformationConfig,
                                           List<DataPoint> expectedDataPoints, boolean ignoreTimestamp) {
    var dataPoints = executeLoadAndTransformTest(persistedData, serviceConfig, lookupConfig, transformationConfig, ignoreTimestamp);
    assertResults(dataPoints, expectedDataPoints, ignoreTimestamp);
  }

  private void testLoadAndTransformFailure(List<List<List<Object>>> persistedData, InfluxDbStorageConfiguration serviceConfig,
                                           InfluxDbStorageConfiguration lookupConfig, InfluxDbStorageConfiguration transformationConfig,
                                           Class<? extends Throwable> expectedException, Class<? extends Throwable> cause) {
    assertThatThrownBy(() -> executeLoadAndTransformTest(persistedData,
        serviceConfig,
        lookupConfig,
        transformationConfig,
        false)
    ).isInstanceOf(expectedException)
        .hasCauseInstanceOf(cause);
  }

  private List<DataPoint> executeLoadAndTransformTest(List<List<List<Object>>> persistedData, InfluxDbStorageConfiguration serviceConfig,
                                                      InfluxDbStorageConfiguration lookupConfig, InfluxDbStorageConfiguration transformationConfig,
                                                      boolean ignoreTimestamp) {
    var service = getInfluxDbStorageServiceSpy(persistedData);
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
