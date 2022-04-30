package org.tsdl.service.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tsdl.infrastructure.api.StorageService;
import org.tsdl.service.configuration.BeanConfiguration;
import org.tsdl.service.service.StorageServiceResolverService;
import org.tsdl.storage.csv.CsvStorageService;
import org.tsdl.storage.influxdb.InfluxDbStorageService;

import java.util.Map;

@Service
public class StorageServiceResolverServiceImpl implements StorageServiceResolverService {
    private final Map<String, StorageService<?, ?, ?>> storageServices;

    @Autowired
    public StorageServiceResolverServiceImpl(Map<String, StorageService<?, ?, ?>> storageServices) {
        this.storageServices = storageServices;
    }

    @Override
    public CsvStorageService resolveCsv() {
        return (CsvStorageService) storageServices.get(BeanConfiguration.CSV_STORAGE_BEAN);
    }

    @Override
    public InfluxDbStorageService resolveInfluxDb() {
        return (InfluxDbStorageService) storageServices.get(BeanConfiguration.INFLUXDB_STORAGE_BEAN);
    }

    @Override
    public <T extends StorageService<?, ?, ?>> T resolve(String name, Class<T> clazz) {
        return clazz.cast(storageServices.get(name));
    }
}
