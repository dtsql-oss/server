package org.tsdl.infrastructure.api;

import java.io.IOException;

public interface StorageService<T, U extends StorageServiceConfiguration<V>, V extends Enum<V>> extends AutoCloseable {
    // can be  general setup specific to the storage (e.g. connect to database, checkIsTrue I/O availability, ...)
    void initialize(U serviceConfiguration);

    boolean isInitialized();

    void store(U storageConfiguration) throws IOException;

    Iterable<T> load(U lookupConfiguration) throws IOException;
}
