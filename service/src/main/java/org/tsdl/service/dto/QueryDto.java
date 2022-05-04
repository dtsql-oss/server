package org.tsdl.service.dto;

import lombok.Data;

@Data
public class QueryDto {
    private StorageDto storage;
    private String tsdlQuery;
}
