package org.tsdl.infrastructure.api;

import org.tsdl.infrastructure.model.DataPoint;

import java.io.IOException;
import java.util.List;

public interface StorageService<T, U extends StorageServiceConfiguration<V>, V extends Enum<V>> extends AutoCloseable {
    // can be  general setup specific to the storage (e.g. connect to database, check I/O availability, ...)
    void initialize(U serviceConfiguration);

    boolean isInitialized();

    void store(U persistConfiguration) throws IOException;

    List<T> load(U lookupConfiguration) throws IOException;

    List<DataPoint> transform(List<T> loadedData, U transformationConfiguration);
}
