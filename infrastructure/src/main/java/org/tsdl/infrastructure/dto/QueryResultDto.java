package org.tsdl.infrastructure.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.infrastructure.model.QueryResultType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryResultDto {
  @NotNull
  @Valid
  QueryResult result;

  @NotNull
  QueryResultType type;
}
