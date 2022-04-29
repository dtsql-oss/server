package org.tsdl.storage.influxdb;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxTable;
import org.tsdl.infrastructure.api.StorageService;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

public class InfluxDbStorageService implements StorageService<FluxTable, InfluxDbStorageConfiguration, InfluxDbStorageProperty> {
    private InfluxDBClient dbClient;
    private QueryApi queryApi;

    @Override
    public void initialize(InfluxDbStorageConfiguration serviceConfiguration) {
        Conditions.checkIsTrue(Condition.ARGUMENT,
          serviceConfiguration.isPropertySet(InfluxDbStorageProperty.URL),
          "URL property is required to initialize InfluxDB storage service.");
        Conditions.checkIsTrue(Condition.ARGUMENT,
          serviceConfiguration.isPropertySet(InfluxDbStorageProperty.TOKEN),
          "Token property is required to initialize InfluxDB storage service.");
        Conditions.checkIsTrue(Condition.ARGUMENT,
          serviceConfiguration.isPropertySet(InfluxDbStorageProperty.ORGANIZATION),
          "Organization property is required to initialize InfluxDB storage service.");
        Conditions.checkIsTrue(Condition.ARGUMENT,
          serviceConfiguration.isPropertySet(InfluxDbStorageProperty.BUCKET),
          "Bucket property is required to initialize InfluxDB storage service.");

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
    public Iterable<FluxTable> load(InfluxDbStorageConfiguration lookupConfiguration) {
        Conditions.checkIsTrue(Condition.STATE, isInitialized(), "InfluxDB service has not been initialized yet. Call initialize() beforehand.");
        Conditions.checkNotNull(Condition.ARGUMENT, lookupConfiguration, "The lookup configuration must not be null.");
        Conditions.checkIsTrue(Condition.ARGUMENT,
          lookupConfiguration.isPropertySet(InfluxDbStorageProperty.QUERY),
          "Query property is required to execute a query with the InfluxDB storage service.");

        return queryApi.query(lookupConfiguration.getProperty(InfluxDbStorageProperty.QUERY, String.class));

    }

    @Override
    public void close() {
        if (dbClient != null) {
            dbClient.close();
        }
    }
}
