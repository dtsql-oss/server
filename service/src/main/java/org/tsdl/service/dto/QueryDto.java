package org.tsdl.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class QueryDto {
    @NotNull
    @Valid
    private StorageDto storage;

    @NotNull
    private String tsdlQuery;
}
