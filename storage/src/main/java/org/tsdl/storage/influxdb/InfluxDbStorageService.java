package org.tsdl.storage.influxdb;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxTable;
import org.tsdl.infrastructure.api.StorageService;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public final class InfluxDbStorageService implements StorageService<FluxTable, InfluxDbStorageConfiguration> {

    // influx uses rfc3339 timestamps (https://docs.influxdata.com/flux/v0.x/data-types/basic/time/#time-syntax)
    private static final DateTimeFormatter INFLUX_TIME_FORMATTER = DateTimeFormatter.ISO_INSTANT;

    private static final String LOAD_RANGE_QUERY_TEMPLATE = """
      from(bucket: "%s")
        |> range(start: time(v: "%s"), stop: time(v: "%s"))
      """;

    InfluxDBClient dbClient;

    QueryApi queryApi;

    @Override
    public void initialize(InfluxDbStorageConfiguration serviceConfiguration) {
        Conditions.checkIsTrue(Condition.ARGUMENT,
          serviceConfiguration.isPropertySet(InfluxDbStorageProperty.URL),
          "'URL' property is required to initialize InfluxDB storage service.");
        Conditions.checkIsTrue(Condition.ARGUMENT,
          serviceConfiguration.isPropertySet(InfluxDbStorageProperty.TOKEN),
          "'TOKEN' property is required to initialize InfluxDB storage service.");
        Conditions.checkIsTrue(Condition.ARGUMENT,
          serviceConfiguration.isPropertySet(InfluxDbStorageProperty.ORGANIZATION),
          "'ORGANIZATION' property is required to initialize InfluxDB storage service.");

        initializeInternal(serviceConfiguration);
    }

    @Override
    public boolean isInitialized() {
        return dbClient != null && queryApi != null;
    }

    @Override
    public void store(InfluxDbStorageConfiguration persistConfiguration) {
        Conditions.checkIsTrue(Condition.STATE, isInitialized(), "InfluxDB service has not been initialized yet. Call initialize() beforehand.");
        Conditions.checkNotNull(Condition.ARGUMENT, persistConfiguration, "The persist configuration must not be null.");
        Conditions.checkIsTrue(Condition.ARGUMENT,
          persistConfiguration.isPropertySet(InfluxDbStorageProperty.BUCKET),
          "'BUCKET' property is required to store data with the InfluxDB storage service.");

        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<FluxTable> load(InfluxDbStorageConfiguration lookupConfiguration) {
        Conditions.checkIsTrue(Condition.STATE, isInitialized(), "InfluxDB service has not been initialized yet. Call initialize() beforehand.");
        Conditions.checkNotNull(Condition.ARGUMENT, lookupConfiguration, "The lookup configuration must not be null.");
        Conditions.checkIsTrue(Condition.ARGUMENT,
          lookupConfiguration.isPropertySet(InfluxDbStorageProperty.QUERY) ^
            (lookupConfiguration.isPropertySet(InfluxDbStorageProperty.BUCKET) &&
              lookupConfiguration.isPropertySet(InfluxDbStorageProperty.LOAD_FROM) &&
              lookupConfiguration.isPropertySet(InfluxDbStorageProperty.LOAD_UNTIL)),
          "Either 'QUERY' property or (exclusively) 'BUCKET', 'LOAD_FROM' and 'LOAD_UNTIL' properties are required to load data with the InfluxDB storage service.");

        String query;
        if (lookupConfiguration.isPropertySet(InfluxDbStorageProperty.QUERY)) {
            query = lookupConfiguration.getProperty(InfluxDbStorageProperty.QUERY, String.class);
        } else {
            var bucket = lookupConfiguration.getProperty(InfluxDbStorageProperty.BUCKET, String.class);
            var from = INFLUX_TIME_FORMATTER.format(lookupConfiguration.getProperty(InfluxDbStorageProperty.LOAD_FROM, Instant.class));
            var to = INFLUX_TIME_FORMATTER.format(lookupConfiguration.getProperty(InfluxDbStorageProperty.LOAD_UNTIL, Instant.class));
            query = LOAD_RANGE_QUERY_TEMPLATE.formatted(bucket, from, to);
        }

        return queryApi.query(query);
    }

    @Override
    public List<DataPoint> transform(List<FluxTable> loadedData, InfluxDbStorageConfiguration transformationConfiguration) {
        Conditions.checkNotNull(Condition.ARGUMENT, transformationConfiguration, "The transformation configuration must not be null.");
        Conditions.checkNotNull(Condition.ARGUMENT, loadedData, "Data to transform must not be null.");
        Conditions.checkIsTrue(Condition.ARGUMENT,
          transformationConfiguration.isPropertySet(InfluxDbStorageProperty.TABLE_INDEX),
          "'TABLE_INDEX' property is required to transform data loaded by the InfluxDB storage service into data points.");

        var tableIndex = transformationConfiguration.getProperty(InfluxDbStorageProperty.TABLE_INDEX, Integer.class);
        if (tableIndex == -1) {
            return loadedData.stream()
              .flatMap(this::transformInfluxDbRecords)
              .toList();
        } else {
            Conditions.checkValidIndex(Condition.ARGUMENT,
              loadedData,
              tableIndex,
              "Index of table to transform into data points must be within range (0..%s).",
              loadedData.size() - 1);
            return transformInfluxDbRecords(loadedData.get(tableIndex))
              .toList();
        }
    }

    @Override
    public void close() {
        if (dbClient != null) {
            dbClient.close();
        }
    }

    void initializeInternal(InfluxDbStorageConfiguration serviceConfiguration) {
        dbClient = InfluxDBClientFactory.create(
          serviceConfiguration.getProperty(InfluxDbStorageProperty.URL, String.class),
          serviceConfiguration.getProperty(InfluxDbStorageProperty.TOKEN, char[].class),
          serviceConfiguration.getProperty(InfluxDbStorageProperty.ORGANIZATION, String.class),
          serviceConfiguration.getProperty(InfluxDbStorageProperty.BUCKET, String.class)
        );

        queryApi = dbClient.getQueryApi();
    }

    private Stream<DataPoint> transformInfluxDbRecords(FluxTable recordStream) {
        return recordStream.getRecords().stream()
          .map(dataRecord -> DataPoint.of(
              dataRecord.getTime(),
              Double.valueOf(Objects.requireNonNull(dataRecord.getValue()).toString())
            )
          );
    }
}
