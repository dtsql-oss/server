package org.tsdl.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.service.dto.QueryResultDto;

@Mapper
public abstract class QueryResultMapper {
  @Mapping(target = "type", expression = "java(result.type())")
  public abstract QueryResultDto entityToDto(QueryResult result);
}
