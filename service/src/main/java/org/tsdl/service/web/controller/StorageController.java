package org.tsdl.service.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.service.dto.StorageReadDto;
import org.tsdl.service.dto.StorageWriteDto;
import org.tsdl.service.exception.ServiceResolutionException;
import org.tsdl.service.mapper.StorageServiceConfigurationMapper;
import org.tsdl.service.service.StorageResolverService;

@RestController
@RequestMapping("/storage")
@Tag(name = "Storage Solution Access", description = "Endpoint exposing storage interface implementations supported by the TSDL system.")
@Validated
@Slf4j
public class StorageController extends BaseController {
  private final StorageResolverService storageServiceResolver;

  @Autowired
  public StorageController(StorageResolverService storageServiceResolver, StorageServiceConfigurationMapper storageServiceConfigurationMapper) {
    super(storageServiceConfigurationMapper);
    this.storageServiceResolver = storageServiceResolver;
  }

  @PostMapping("{storageName}/read")
  @Operation(summary = "Load and transform data using a given storage mechanism implementation.")
  @ApiResponse(responseCode = "200", description = "Data has been loaded successfully.")
  @ApiResponse(responseCode = "400", description = "Specified storage is not supported.")
  public List<DataPoint> read(@PathVariable String storageName, @Valid @RequestBody StorageReadDto storage) throws ServiceResolutionException {
    log.info("Received read request for storage '{}'", storageName);
    log.debug("Service configuration: {}", storage.getServiceConfiguration());
    log.debug("Lookup configuration: {}", storage.getLookupConfiguration());
    log.debug("Transformation configuration: {}", storage.getTransformationConfiguration());

    var tsdlStorage = storageServiceResolver.resolve(storageName);

    var serviceConfig = mapConfig(storage.getServiceConfiguration(), tsdlStorage);
    var lookupConfig = mapConfig(storage.getLookupConfiguration(), tsdlStorage);
    var transformationConfig = mapConfig(storage.getTransformationConfiguration(), tsdlStorage);

    tsdlStorage.storageService().initialize(serviceConfig);
    var fetchedData = tsdlStorage.storageService().load(lookupConfig);

    return tsdlStorage.storageService().transform(fetchedData, transformationConfig);
  }

  @PostMapping("{storageName}/write")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Store data using a given storage mechanism implementation.")
  @ApiResponse(responseCode = "200", description = "Data has been stored successfully.")
  @ApiResponse(responseCode = "400", description = "Specified storage is not supported.")
  public void write(@PathVariable String storageName, @Valid @RequestBody StorageWriteDto payload) throws ServiceResolutionException {
    log.info("Received write request for storage '{}'", storageName);
    log.debug("Service configuration: {}", payload.getServiceConfiguration());
    log.debug("Persist configuration: {}", payload.getPersistConfiguration());

    var tsdlStorage = storageServiceResolver.resolve(storageName);

    var serviceConfig = mapConfig(payload.getServiceConfiguration(), tsdlStorage);
    var persistConfiguration = mapConfig(payload.getPersistConfiguration(), tsdlStorage);

    tsdlStorage.storageService().initialize(serviceConfig);
    tsdlStorage.storageService().store(payload.getData(), persistConfiguration);
  }
}
