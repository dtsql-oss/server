package org.tsdl.storage.influxdb;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class InfluxDbStorageServiceTest {

    // TODO include mockito with ability to add static mocks, mock InfluxDBClient and return stub data

    @Test
    void load_viaQuery() {
        var serviceConfig = new InfluxDbStorageConfiguration(Map.of(
          InfluxDbStorageProperty.URL, "http://localhost:8086",
          InfluxDbStorageProperty.ORGANIZATION, "tuwien-corec",
          InfluxDbStorageProperty.TOKEN, "Bawfa5LFDhUM5yjlmErFbZPtAT4jeOxtTvgdXbCxCjy5rPG-SR5IRdR_aTYKqr3xvoN49VroZn9YfuwVQCp34A==".toCharArray(),
          InfluxDbStorageProperty.BUCKET, "bucket0"
        ));
        var lookupConfig = new InfluxDbStorageConfiguration(Map.of(
          InfluxDbStorageProperty.QUERY, """
            from(bucket: "bucket0")
                        |> range(start: time(v: "2016-01-01T00:00:00Z"), stop: time(v: "2019-12-31T23:59:59Z"))
                        |> filter(fn: (r) => r["_measurement"] == "productionOutputs")
                        |> filter(fn: (r) => r["_field"] == "output")
                        |> yield(name: "measuredValue")
            """
        ));
        var transformationConfig = new InfluxDbStorageConfiguration(Map.of(
          InfluxDbStorageProperty.TABLE_INDEX, 4
        ));

        try (var service = new InfluxDbStorageService()) {
            service.initialize(serviceConfig);
            var tables = service.load(lookupConfig);
            var dataPoints = service.transform(tables, transformationConfig);
            dataPoints.forEach(pt -> System.out.println(pt.toString()));
        }
    }

    @Test
    void load_viaBucketAndRange() {
        var serviceConfig = new InfluxDbStorageConfiguration(Map.of(
          InfluxDbStorageProperty.URL, "http://localhost:8086",
          InfluxDbStorageProperty.ORGANIZATION, "tuwien-corec",
          InfluxDbStorageProperty.TOKEN, "Bawfa5LFDhUM5yjlmErFbZPtAT4jeOxtTvgdXbCxCjy5rPG-SR5IRdR_aTYKqr3xvoN49VroZn9YfuwVQCp34A==".toCharArray(),
          InfluxDbStorageProperty.BUCKET, "bucket0"
        ));
        var lookupConfig = new InfluxDbStorageConfiguration(Map.of(
          InfluxDbStorageProperty.BUCKET, "bucket0",
          InfluxDbStorageProperty.LOAD_FROM, Instant.parse("2016-01-01T00:00:00Z"),
          InfluxDbStorageProperty.LOAD_UNTIL, Instant.parse("2019-12-31T23:59:59Z")
        ));
        var lookupConfig2 = new InfluxDbStorageConfiguration(Map.of(
          InfluxDbStorageProperty.QUERY, """
            from(bucket: "bucket0")
                        |> range(start: time(v: "2016-01-01T00:00:00Z"), stop: time(v: "2019-12-31T23:59:59Z"))
                        |> filter(fn: (r) => r["_measurement"] == "productionOutputs")
                        |> filter(fn: (r) => r["_field"] == "output")
                        |> yield(name: "measuredValue")
            """
        ));
        var transformationConfig = new InfluxDbStorageConfiguration(Map.of(
          InfluxDbStorageProperty.TABLE_INDEX, 4
        ));

        try (var service = new InfluxDbStorageService()) {
            service.initialize(serviceConfig);
            var tables = service.load(lookupConfig);
            var dataPoints = service.transform(tables, transformationConfig);
            dataPoints.forEach(pt -> System.out.println(pt.toString()));

            var tables2 = service.load(lookupConfig2);
            var dataPoints2 = service.transform(tables2, transformationConfig);
            assertThat(dataPoints)
              .hasSize(dataPoints2.size())
              .hasSameElementsAs(dataPoints2);
        }
    }
}
