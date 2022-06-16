package org.tsdl.service.service.impl;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tsdl.infrastructure.api.StorageServiceConfiguration;
import org.tsdl.service.exception.UnknownStorageException;
import org.tsdl.service.model.TsdlStorage;
import org.tsdl.service.service.StorageResolverService;

/**
 * Default implementation of {@link StorageResolverService}.
 */
@Service
public class StorageResolverServiceImpl implements StorageResolverService {
  private static final String STORAGE_BEAN_NAME_TEMPLATE = "storage.%s";

  private final Map<String, TsdlStorage<?, ? extends StorageServiceConfiguration>> storageServices;

  @Autowired
  public StorageResolverServiceImpl(Map<String, TsdlStorage<?, ? extends StorageServiceConfiguration>> storageServices) {
    this.storageServices = storageServices;
  }

  @Override
  @SuppressWarnings("unchecked") // unchecked operation required due to type erasure
  public TsdlStorage<Object, StorageServiceConfiguration> resolve(String storageIdentifier) throws UnknownStorageException {
    var storageBeanReference = STORAGE_BEAN_NAME_TEMPLATE.formatted(storageIdentifier);
    if (!storageServices.containsKey(storageBeanReference)) {
      throw new UnknownStorageException("Storage '%s' is not supported".formatted(storageIdentifier));
    }
    return (TsdlStorage<Object, StorageServiceConfiguration>) storageServices.get(storageBeanReference);
  }
}
