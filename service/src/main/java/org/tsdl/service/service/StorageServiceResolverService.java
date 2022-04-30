package org.tsdl.service.service;

import org.tsdl.infrastructure.api.StorageService;
import org.tsdl.storage.csv.CsvStorageService;
import org.tsdl.storage.influxdb.InfluxDbStorageService;

public interface StorageServiceResolverService {
    CsvStorageService resolveCsv();

    InfluxDbStorageService resolveInfluxDb();

    <T extends StorageService<?, ?, ?>> T resolve(String name, Class<T> clazz);
}
