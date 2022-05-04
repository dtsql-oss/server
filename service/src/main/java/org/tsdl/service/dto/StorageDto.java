package org.tsdl.service.dto;

import lombok.Data;

import java.util.Map;

@Data
public class StorageDto {
    private String name;
    private Map<String, Object> serviceConfiguration;
    private Map<String, Object> lookupConfiguration;
    private Map<String, Object> persistConfiguration;
    private Map<String, Object> transformationConfiguration;
}
