package org.tsdl.service.model;

import org.tsdl.infrastructure.api.StorageService;
import org.tsdl.infrastructure.api.StorageServiceConfiguration;

import java.util.function.Supplier;

public record TsdlStorage<T, U extends StorageServiceConfiguration>(
  StorageService<T, U> storageService,
  Supplier<StorageServiceConfiguration> configurationSupplier,
  Class<? extends Enum<?>> propertyClass
) {
}
