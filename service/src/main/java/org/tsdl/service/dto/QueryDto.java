package org.tsdl.service.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class QueryDto {
  @NotNull
  @Valid
  private StorageDto storage;

  @NotNull
  private String tsdlQuery;
}
