package org.tsdl.service.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tsdl.infrastructure.api.QueryService;
import org.tsdl.infrastructure.dto.QueryDto;
import org.tsdl.infrastructure.dto.QueryResultDto;
import org.tsdl.service.exception.ServiceResolutionException;
import org.tsdl.service.mapper.QueryResultMapper;
import org.tsdl.service.mapper.StorageServiceConfigurationMapper;
import org.tsdl.service.service.StorageResolverService;
import org.tsdl.storage.csv.CsvStorageProperty;

@RestController
@RequestMapping("/query")
@Tag(name = "TSDL Query", description = "Endpoint exposing TSDL query services for generic storage implementations.")
@Validated
@Slf4j
public class QueryController extends BaseController {
  private final StorageResolverService storageServiceResolver;
  private final QueryResultMapper queryResultMapper;

  private final QueryService queryService;

  @Autowired
  public QueryController(StorageResolverService storageServiceResolver, StorageServiceConfigurationMapper storageServiceConfigurationMapper,
                         QueryResultMapper queryResultMapper, QueryService queryService) {
    super(storageServiceConfigurationMapper);
    this.storageServiceResolver = storageServiceResolver;
    this.queryResultMapper = queryResultMapper;
    this.queryService = queryService;
  }

  @PostMapping
  @Operation(summary = "Execute query over configurable storage provider.")
  @ApiResponse(responseCode = "200", description = "Query was executed successfully.")
  @ApiResponse(responseCode = "400", description = "Specified storage is not supported.")
  public QueryResultDto query(@Valid @RequestBody
                              @Parameter(description = "Specification of query to execute, i.e., TSDL query and storage configuration.")
                              QueryDto querySpecification) throws ServiceResolutionException, IOException {
    log.info("Received query request for storage '{}'", querySpecification.getStorage().getName());
    log.debug("Service configuration: {}", querySpecification.getStorage().getServiceConfiguration());
    log.debug("Lookup configuration: {}", querySpecification.getStorage().getLookupConfiguration());
    log.debug("Transformation configuration: {}", querySpecification.getStorage().getTransformationConfiguration());

    var storageSpec = querySpecification.getStorage();
    var tsdlStorage = storageServiceResolver.resolve(storageSpec.getName());

    var serviceConfig = mapConfig(storageSpec.getServiceConfiguration(), tsdlStorage);
    var lookupConfig = mapConfig(storageSpec.getLookupConfiguration(), tsdlStorage);
    var transformationConfig = mapConfig(storageSpec.getTransformationConfiguration(), tsdlStorage);

    var targetFile = serviceConfig.getProperty(CsvStorageProperty.TARGET_FILE, String.class);
    if (serviceConfig.isPropertySet(CsvStorageProperty.TARGET_FILE)) {
      log(targetFile, "QUERY:\n" + querySpecification.getTsdlQuery() + "\n");
    }

    tsdlStorage.storageService().initialize(serviceConfig);
    var fetchedData = tsdlStorage.storageService().load(lookupConfig);
    var dataPoints = tsdlStorage.storageService().transform(fetchedData, transformationConfig);

    var beginMillis = System.currentTimeMillis();
    var beginNano = System.nanoTime();
    var beginInstant = Instant.now();
    var queryResult = queryService.query(dataPoints, querySpecification.getTsdlQuery());
    var endMillis = System.currentTimeMillis();
    var endNano = System.nanoTime();
    var endInstant = Instant.now();
    var durationMillis = endMillis - beginMillis;
    var durationNano = TimeUnit.NANOSECONDS.toMillis(endNano - beginNano);
    var durationInstant = Duration.between(beginInstant, endInstant).toMillis();
    var durationAveraged = (durationMillis + durationNano + durationInstant) / 3;

    if (serviceConfig.isPropertySet(CsvStorageProperty.TARGET_FILE)) {
      log(targetFile,
          ("""

              DURATIONS:
                from millis: %s ms
                from nanoTime: %s ms
                from instant: %s ms
                averaged: %s
              -------------------------------------------------------------------------------
              """).formatted(
              durationMillis, durationNano,
              durationInstant, durationAveraged));
    }

    return queryResultMapper.entityToDto(queryResult);
  }

  private void log(String targetFile, String text) throws IOException {
    var path = Path.of(targetFile);
    Files.createDirectories(path.getParent());
    Files.writeString(
        path,
        text,
        StandardCharsets.UTF_8,
        StandardOpenOption.CREATE, StandardOpenOption.APPEND
    );
  }
}
