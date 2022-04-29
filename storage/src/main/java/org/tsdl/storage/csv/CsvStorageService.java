package org.tsdl.storage.csv;

import org.tsdl.infrastructure.api.StorageService;

public class CsvStorageService implements StorageService<Object, CsvStorageConfiguration, CsvStorageProperty> {


    @Override
    public void initialize(CsvStorageConfiguration serviceConfiguration) {

    }

    @Override
    public boolean isInitialized() {
        return true;
    }

    @Override
    public void store(CsvStorageConfiguration storageConfiguration) {

    }

    @Override
    public Iterable<Object> load(CsvStorageConfiguration lookupConfiguration) {
        return null;
    }
}
