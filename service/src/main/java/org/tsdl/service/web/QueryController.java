package org.tsdl.service.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tsdl.infrastructure.api.QueryService;
import org.tsdl.infrastructure.api.StorageServiceConfiguration;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.service.dto.QueryDto;
import org.tsdl.service.exception.InputInterpretationException;
import org.tsdl.service.exception.UnknownStorageException;
import org.tsdl.service.mapper.StorageServiceConfigurationMapper;
import org.tsdl.service.model.TsdlStorage;
import org.tsdl.service.service.StorageResolverService;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/query")
@Tag(name = "TSDL Query", description = "Endpoint exposing TSDL query services for generic storage implementations.")
@Validated
public class QueryController {
    private final StorageResolverService storageServiceResolver;
    private final StorageServiceConfigurationMapper storageServiceConfigurationMapper;

    private final QueryService queryService;

    @Autowired
    public QueryController(StorageResolverService storageServiceResolver, StorageServiceConfigurationMapper storageServiceConfigurationMapper, QueryService queryService) {
        this.storageServiceResolver = storageServiceResolver;
        this.storageServiceConfigurationMapper = storageServiceConfigurationMapper;
        this.queryService = queryService;
    }

    @PostMapping
    @Operation(summary = "Execute query")
    @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Query was executed successfully.")
    })
    public List<DataPoint> execute(@Valid @RequestBody
                                   @Parameter(description = "Specification of query to execute, i.e., TSDL query and storage configuration.")
                                   QueryDto querySpecification) throws UnknownStorageException, InputInterpretationException, IOException {
        var storageSpec = querySpecification.getStorage();
        var tsdlStorage = storageServiceResolver.resolve(storageSpec.getName());

        var serviceConfig = mapConfig(storageSpec.getServiceConfiguration(), tsdlStorage);
        var lookupConfig = mapConfig(storageSpec.getLookupConfiguration(), tsdlStorage);
        var transformationConfig = mapConfig(storageSpec.getTransformationConfiguration(), tsdlStorage);

        tsdlStorage.storageService().initialize(serviceConfig);
        var fetchedData = tsdlStorage.storageService().load(lookupConfig);
        var dataPoints = tsdlStorage.storageService().transform(fetchedData, transformationConfig);

        var queriedData = queryService.query(dataPoints, querySpecification.getTsdlQuery());
        return queriedData.getItems();
    }

    private StorageServiceConfiguration mapConfig(Map<String, Object> properties, TsdlStorage<Object, StorageServiceConfiguration> targetStorage)
      throws InputInterpretationException {
        return storageServiceConfigurationMapper.mapToConfiguration(properties, targetStorage.configurationSupplier(), targetStorage.propertyClass());
    }
}
