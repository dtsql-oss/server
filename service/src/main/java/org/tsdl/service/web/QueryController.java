package org.tsdl.service.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.tsdl.infrastructure.api.QueryService;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.service.configuration.BeanConfiguration;
import org.tsdl.service.dto.QueryDto;
import org.tsdl.service.exception.InputInterpretationException;
import org.tsdl.service.exception.UnknownStorageException;
import org.tsdl.service.mapper.StorageServiceConfigurationMapper;
import org.tsdl.service.service.StorageServiceResolverService;
import org.tsdl.storage.csv.CsvStorageService;
import org.tsdl.storage.influxdb.InfluxDbStorageConfiguration;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/query")
@Tag(name = "TSDL Query", description = "Endpoint exposing TSDL query services for generic storage implementations.")
public class QueryController {
    private final StorageServiceResolverService storageServiceResolverService;
    private final StorageServiceConfigurationMapper storageServiceConfigurationMapper;

    private final QueryService queryService;

    @Autowired
    public QueryController(StorageServiceResolverService storageServiceResolverService, StorageServiceConfigurationMapper storageServiceConfigurationMapper, QueryService queryService) {
        this.storageServiceResolverService = storageServiceResolverService;
        this.storageServiceConfigurationMapper = storageServiceConfigurationMapper;
        this.queryService = queryService;
    }

    @PostMapping
    @Operation(summary = "Execute query")
    @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Query was executed successfully.")
    })
    public List<DataPoint> execute(@RequestBody QueryDto querySpecification) throws UnknownStorageException, InputInterpretationException, IOException {
        var referenceMap = Map.of(
          BeanConfiguration.CSV_STORAGE_BEAN, CsvStorageService.class,
          BeanConfiguration.INFLUXDB_STORAGE_BEAN, InfluxDbStorageConfiguration.class
        );

        var storageBeanReference = "storage.%s".formatted(querySpecification.getStorage().getName());
        if (BeanConfiguration.CSV_STORAGE_BEAN.equals(storageBeanReference)) {
            var storage = storageServiceResolverService.resolveCsv();
            var serviceConfig = storageServiceConfigurationMapper.mapToCsvConfiguration(querySpecification.getStorage().getServiceConfiguration());
            var lookupConfig = storageServiceConfigurationMapper.mapToCsvConfiguration(querySpecification.getStorage().getLookupConfiguration());
            var transformationConfig = storageServiceConfigurationMapper.mapToCsvConfiguration(querySpecification.getStorage().getTransformationConfiguration());

            storage.initialize(serviceConfig);
            var rows = storage.load(lookupConfig);
            var dataPoints = storage.transform(rows,transformationConfig);
            return queryService.query(dataPoints, querySpecification.getTsdlQuery()).getItems();
        } else if (BeanConfiguration.INFLUXDB_STORAGE_BEAN.equals(storageBeanReference)) {
            // var dd = storageServiceResolverService.resolve(storageBeanReference, referenceMap.get(storageBeanReference));
            var storage = storageServiceResolverService.resolveInfluxDb();
            var serviceConfig = storageServiceConfigurationMapper.mapToInfluxDbConfiguration(querySpecification.getStorage().getServiceConfiguration());
            var lookupConfig = storageServiceConfigurationMapper.mapToInfluxDbConfiguration(querySpecification.getStorage().getLookupConfiguration());
            var transformationConfig = storageServiceConfigurationMapper.mapToInfluxDbConfiguration(querySpecification.getStorage().getTransformationConfiguration());

            storage.initialize(serviceConfig);
            var tables = storage.load(lookupConfig);
            var dataPoints = storage.transform(tables,transformationConfig);
            return queryService.query(dataPoints, querySpecification.getTsdlQuery()).getItems();
        } else {
            throw new UnknownStorageException("Storage '%s' is not supported".formatted(querySpecification.getStorage().getName()));
        }
    }

    @GetMapping
    @Operation(summary = "Retrieve greeting")
    @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Greeting was conducted successfully."),
    })
    public String world() {
        return "Hello, World!";
    }

    @GetMapping("fail")
    @Operation(summary = "Causes internal server error")
    @ApiResponses({
      @ApiResponse(responseCode = "500", description = "Endpoint invocation caused internal server error.")
    })
    public String fail() {
        return "unreachable";
    }
}
