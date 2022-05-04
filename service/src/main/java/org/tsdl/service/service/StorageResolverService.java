package org.tsdl.service.service;

import org.tsdl.infrastructure.api.StorageServiceConfiguration;
import org.tsdl.service.exception.UnknownStorageException;
import org.tsdl.service.model.TsdlStorage;

public interface StorageResolverService {
    TsdlStorage<Object, StorageServiceConfiguration> resolve(String storageIdentifier) throws UnknownStorageException;
}
