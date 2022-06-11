package org.tsdl.service.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.Map;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tsdl.infrastructure.api.QueryService;
import org.tsdl.infrastructure.api.StorageServiceConfiguration;
import org.tsdl.service.dto.QueryDto;
import org.tsdl.service.dto.QueryResultDto;
import org.tsdl.service.exception.UnknownStorageException;
import org.tsdl.service.mapper.QueryResultMapper;
import org.tsdl.service.mapper.StorageServiceConfigurationMapper;
import org.tsdl.service.model.TsdlStorage;
import org.tsdl.service.service.StorageResolverService;

@RestController
@RequestMapping("/query")
@Tag(name = "TSDL Query", description = "Endpoint exposing TSDL query services for generic storage implementations.")
@Validated
@Slf4j
public class QueryController {
  private final StorageResolverService storageServiceResolver;
  private final StorageServiceConfigurationMapper storageServiceConfigurationMapper;
  private final QueryResultMapper queryResultMapper;

  private final QueryService queryService;

  @Autowired
  public QueryController(StorageResolverService storageServiceResolver, StorageServiceConfigurationMapper storageServiceConfigurationMapper,
                         QueryResultMapper queryResultMapper, QueryService queryService) {
    this.storageServiceResolver = storageServiceResolver;
    this.storageServiceConfigurationMapper = storageServiceConfigurationMapper;
    this.queryResultMapper = queryResultMapper;
    this.queryService = queryService;
  }

  @PostMapping
  @Operation(summary = "Execute query over configurable storage provider.")
  @ApiResponse(responseCode = "200", description = "Query was executed successfully.")
  public QueryResultDto query(@Valid @RequestBody
                           @Parameter(description = "Specification of query to execute, i.e., TSDL query and storage configuration.")
                           QueryDto querySpecification) throws UnknownStorageException, IOException {
    log.info("Received query request for storage '{}'", querySpecification.getStorage().getName());
    log.debug("Service configuration: {}", querySpecification.getStorage().getServiceConfiguration());
    log.debug("Lookup configuration: {}", querySpecification.getStorage().getLookupConfiguration());
    log.debug("Transformation configuration: {}", querySpecification.getStorage().getTransformationConfiguration());

    var storageSpec = querySpecification.getStorage();
    var tsdlStorage = storageServiceResolver.resolve(storageSpec.getName());

    var serviceConfig = mapConfig(storageSpec.getServiceConfiguration(), tsdlStorage);
    var lookupConfig = mapConfig(storageSpec.getLookupConfiguration(), tsdlStorage);
    var transformationConfig = mapConfig(storageSpec.getTransformationConfiguration(), tsdlStorage);

    tsdlStorage.storageService().initialize(serviceConfig);
    var fetchedData = tsdlStorage.storageService().load(lookupConfig);
    var dataPoints = tsdlStorage.storageService().transform(fetchedData, transformationConfig);

    var queryResult = queryService.query(dataPoints, querySpecification.getTsdlQuery());
    return queryResultMapper.entityToDto(queryResult);
  }

  private StorageServiceConfiguration mapConfig(Map<String, Object> properties, TsdlStorage<Object, StorageServiceConfiguration> targetStorage) {
    return storageServiceConfigurationMapper.mapToConfiguration(properties, targetStorage.configurationSupplier(), targetStorage.propertyClass());
  }
}
