package org.tsdl.service.model;

import java.util.function.Supplier;
import org.tsdl.infrastructure.api.StorageService;
import org.tsdl.infrastructure.api.StorageServiceConfiguration;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

/**
 * Represents a storage implementation.
 *
 * @param storageService        the underlying {@link StorageService} instance
 * @param configurationSupplier a supplier for the {@link StorageServiceConfiguration} of {@code storageService}
 * @param propertyClass         the containing enumeration representing the properties of the {@link StorageServiceConfiguration} returned
 *                              by {@code configurationSupplier}
 * @param <T>                   type of data retrieved by the {@link StorageService#load(StorageServiceConfiguration)} method
 * @param <U>                   configuration compatible with this {@link StorageService} implementation
 */
public record TsdlStorage<T, U extends StorageServiceConfiguration>(
    StorageService<T, U> storageService,
    Supplier<StorageServiceConfiguration> configurationSupplier,
    Class<? extends Enum<?>> propertyClass
) {

  /**
   * Initializes a {@link TsdlStorage} instance.
   */
  public TsdlStorage {
    Conditions.checkNotNull(Condition.ARGUMENT, storageService, "Storage service instance for TsdlStorage must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, configurationSupplier, "Supplier of storage service configuration for TsdlStorage must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, propertyClass, "Class of storage service configuration properties for TsdlStorage must not be null");
  }
}
