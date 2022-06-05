package org.tsdl.service.model;

import java.util.function.Supplier;
import org.tsdl.infrastructure.api.StorageService;
import org.tsdl.infrastructure.api.StorageServiceConfiguration;

public record TsdlStorage<T, U extends StorageServiceConfiguration>(
    StorageService<T, U> storageService,
    Supplier<StorageServiceConfiguration> configurationSupplier,
    Class<? extends Enum<?>> propertyClass
) {
}
