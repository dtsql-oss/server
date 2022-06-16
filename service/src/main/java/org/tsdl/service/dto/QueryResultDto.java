package org.tsdl.service.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.infrastructure.model.QueryResultType;

@Data
@NoArgsConstructor
public class QueryResultDto {
  @NotNull
  @Valid
  QueryResult result;

  @NotNull
  QueryResultType type;
}
