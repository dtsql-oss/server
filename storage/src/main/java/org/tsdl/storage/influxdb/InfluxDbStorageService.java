package org.tsdl.storage.influxdb;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxTable;
import org.tsdl.infrastructure.api.StorageService;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class InfluxDbStorageService implements StorageService<FluxTable, InfluxDbStorageConfiguration, InfluxDbStorageProperty> {
    private InfluxDBClient dbClient;
    private QueryApi queryApi;

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
        Conditions.checkIsTrue(Condition.ARGUMENT,
          serviceConfiguration.isPropertySet(InfluxDbStorageProperty.BUCKET),
          "'BUCKET' property is required to initialize InfluxDB storage service.");

        dbClient = InfluxDBClientFactory.create(
          serviceConfiguration.getProperty(InfluxDbStorageProperty.URL, String.class),
          serviceConfiguration.getProperty(InfluxDbStorageProperty.TOKEN, char[].class),
          serviceConfiguration.getProperty(InfluxDbStorageProperty.ORGANIZATION, String.class),
          serviceConfiguration.getProperty(InfluxDbStorageProperty.BUCKET, String.class)
        );

        queryApi = dbClient.getQueryApi();
    }

    @Override
    public boolean isInitialized() {
        return dbClient != null && queryApi != null;
    }

    @Override
    public void store(InfluxDbStorageConfiguration storageConfiguration) {
        Conditions.checkIsTrue(Condition.STATE, isInitialized(), "InfluxDB service has not been initialized yet. Call initialize() beforehand.");
        Conditions.checkNotNull(Condition.ARGUMENT, storageConfiguration, "The storage configuration must not be null.");

        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<FluxTable> load(InfluxDbStorageConfiguration lookupConfiguration) {
        Conditions.checkIsTrue(Condition.STATE, isInitialized(), "InfluxDB service has not been initialized yet. Call initialize() beforehand.");
        Conditions.checkNotNull(Condition.ARGUMENT, lookupConfiguration, "The lookup configuration must not be null.");
        Conditions.checkIsTrue(Condition.ARGUMENT,
          lookupConfiguration.isPropertySet(InfluxDbStorageProperty.QUERY),
          "'QUERY' property is required to execute a query with the InfluxDB storage service.");

        return queryApi.query(lookupConfiguration.getProperty(InfluxDbStorageProperty.QUERY, String.class));
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

    private Stream<DataPoint> transformInfluxDbRecords(FluxTable recordStream) {
        return recordStream.getRecords().stream()
          .map(record -> DataPoint.of(
              record.getTime(),
              Double.valueOf(Objects.requireNonNull(record.getValue()).toString())
            )
          );
    }

    @Override
    public void close() {
        if (dbClient != null) {
            dbClient.close();
        }
    }
}
