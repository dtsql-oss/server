package org.tsdl.service.web.controller;

import java.util.Map;
import org.tsdl.infrastructure.api.StorageServiceConfiguration;
import org.tsdl.service.exception.ServiceResolutionException;
import org.tsdl.service.mapper.StorageServiceConfigurationMapper;
import org.tsdl.service.model.TsdlStorage;

abstract class BaseController {
  private final StorageServiceConfigurationMapper storageServiceConfigurationMapper;

  protected BaseController(StorageServiceConfigurationMapper storageServiceConfigurationMapper) {
    this.storageServiceConfigurationMapper = storageServiceConfigurationMapper;
  }

  protected StorageServiceConfiguration mapConfig(Map<String, Object> properties, TsdlStorage<Object, StorageServiceConfiguration> targetStorage)
      throws ServiceResolutionException {
    return storageServiceConfigurationMapper.mapToConfiguration(properties, targetStorage.configurationSupplier(), targetStorage.propertyClass());
  }
}
