package org.tsdl.storage.influxdb;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxTable;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.tsdl.infrastructure.api.StorageService;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.storage.BaseStorageService;

/**
 * An implementation of {@link StorageService} for a storage mechanism targeting data deposited in an InfluxDB instance.
 */
public final class InfluxDbStorageService extends BaseStorageService implements StorageService<FluxTable, InfluxDbStorageConfiguration> {

  // influx uses rfc3339 timestamps (https://docs.influxdata.com/flux/v0.x/data-types/basic/time/#time-syntax)
  private static final DateTimeFormatter INFLUX_TIME_FORMATTER = DateTimeFormatter.ISO_INSTANT;

  public static final String INITIALIZE_PROPERTY_REQUIRED = "'%s' property ('%s') is required to initialize the InfluxDB storage service.";
  public static final String STORE_PROPERTY_REQUIRED = "'%s' property ('%s') is required to store data with the InfluxDB storage service.";
  public static final String LOAD_PROPERTY_REQUIRED = "'%s' property ('%s') is required to load data with the InfluxDB storage service.";
  public static final String TRANSFORMATION_PROPERTY_REQUIRED =
      "'%s' property ('%s') is required to transform data loaded by the InfluxDB storage service into data points.";

  private static final String LOAD_RANGE_QUERY_TEMPLATE = """
      from(bucket: "%s")
        |> range(start: time(v: "%s"), stop: time(v: "%s"))
      """;

  InfluxDBClient dbClient;

  QueryApi queryApi;

  @Override
  public void initialize(InfluxDbStorageConfiguration serviceConfiguration) {
    safeStorageAccess(() -> {
      Conditions.checkNotNull(Condition.ARGUMENT, serviceConfiguration, "The service configuration must not be null.");
      requireProperty(serviceConfiguration, InfluxDbStorageProperty.URL, INITIALIZE_PROPERTY_REQUIRED);
      requireProperty(serviceConfiguration, InfluxDbStorageProperty.TOKEN, INITIALIZE_PROPERTY_REQUIRED);
      requireProperty(serviceConfiguration, InfluxDbStorageProperty.ORGANIZATION, INITIALIZE_PROPERTY_REQUIRED);

      initializeInternal(serviceConfiguration);
    });
  }

  @Override
  public boolean isInitialized() {
    return dbClient != null && queryApi != null;
  }

  @Override
  public void store(List<DataPoint> data, InfluxDbStorageConfiguration persistConfiguration) {
    safeStorageAccess(() -> {
      Conditions.checkIsTrue(Condition.STATE, isInitialized(), "InfluxDB service has not been initialized yet. Call initialize() beforehand.");
      Conditions.checkNotNull(Condition.ARGUMENT, persistConfiguration, "The persist configuration must not be null.");
      requireProperty(persistConfiguration, InfluxDbStorageProperty.BUCKET, STORE_PROPERTY_REQUIRED);

      throw new UnsupportedOperationException("Not implemented yet");
    });
  }

  @Override
  public List<FluxTable> load(InfluxDbStorageConfiguration lookupConfiguration) {
    return safeStorageAccess(() -> {
      Conditions.checkIsTrue(Condition.STATE, isInitialized(), "InfluxDB service has not been initialized yet. Call initialize() beforehand.");
      Conditions.checkNotNull(Condition.ARGUMENT, lookupConfiguration, "The lookup configuration must not be null.");
      Conditions.checkIsTrue(
          Condition.ARGUMENT,
          lookupConfiguration.isPropertySet(InfluxDbStorageProperty.QUERY)
              ^ (lookupConfiguration.isPropertySet(InfluxDbStorageProperty.BUCKET)
              && lookupConfiguration.isPropertySet(InfluxDbStorageProperty.LOAD_FROM)
              && lookupConfiguration.isPropertySet(InfluxDbStorageProperty.LOAD_UNTIL)),
          "Either '%s' property ('%s') or (exclusively) '%s', '%s' and '%s' properties ('%s', '%s' and '%s') are "
              + "required to load data with the InfluxDB storage service.",
          InfluxDbStorageProperty.QUERY.name(), InfluxDbStorageProperty.QUERY.identifier(),
          InfluxDbStorageProperty.BUCKET.name(), InfluxDbStorageProperty.LOAD_FROM.name(), InfluxDbStorageProperty.LOAD_UNTIL.name(),
          InfluxDbStorageProperty.BUCKET.identifier(), InfluxDbStorageProperty.LOAD_FROM.identifier(), InfluxDbStorageProperty.LOAD_UNTIL.identifier()
      );

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
    });
  }

  @Override
  public List<DataPoint> transform(List<FluxTable> loadedData, InfluxDbStorageConfiguration transformationConfiguration) {
    return safeStorageAccess(() -> {
      Conditions.checkNotNull(Condition.ARGUMENT, transformationConfiguration, "The transformation configuration must not be null.");
      Conditions.checkNotNull(Condition.ARGUMENT, loadedData, "Data to transform must not be null.");
      requireProperty(transformationConfiguration, InfluxDbStorageProperty.TABLE_INDEX, TRANSFORMATION_PROPERTY_REQUIRED);

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
    });
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

  private void requireProperty(InfluxDbStorageConfiguration config, InfluxDbStorageProperty property, String messageTemplate) {
    Conditions.checkIsTrue(Condition.ARGUMENT,
        config.isPropertySet(property),
        messageTemplate,
        property.name(), property.identifier());
  }
}
