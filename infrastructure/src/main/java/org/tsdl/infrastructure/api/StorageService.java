package org.tsdl.infrastructure.api;

import java.io.IOException;
import java.util.List;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Represents an abstraction to be used to connect a {@link QueryService} to a custom storage mechanism.
 *
 * @param <T> type of data retrieved by the {@link StorageService#load(StorageServiceConfiguration)} method
 * @param <U> configuration compatible with this {@link StorageService} implementation
 */
public interface StorageService<T, U extends StorageServiceConfiguration> extends AutoCloseable {
  // can be general setup specific to the storage (e.g. connect to database, check I/O availability, ...)
  void initialize(U serviceConfiguration);

  boolean isInitialized();

  void store(List<DataPoint> data, U persistConfiguration) throws IOException;

  List<T> load(U lookupConfiguration) throws IOException;

  List<DataPoint> transform(List<T> loadedData, U transformationConfiguration);
}
