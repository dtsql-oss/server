package org.tsdl.infrastructure.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QueryDto {
  @NotNull
  @Valid
  private StorageDto storage;

  @NotNull
  private String tsdlQuery;
}
