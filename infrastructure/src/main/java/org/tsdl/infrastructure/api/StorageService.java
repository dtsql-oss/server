package org.tsdl.infrastructure.api;

public interface StorageService<T, U extends StorageServiceConfiguration<V>, V extends Enum<V>> {
    // can be  general setup specific to the storage (e.g. connect to database, check I/O availability, ...)
    void initialize(U serviceConfiguration);

    boolean isInitialized();

    void store(U storageConfiguration);

    Iterable<T> load(U lookupConfiguration);
}
