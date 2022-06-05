package org.tsdl.service.model;

import java.util.function.Supplier;
import org.tsdl.infrastructure.api.StorageService;
import org.tsdl.infrastructure.api.StorageServiceConfiguration;

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
}
