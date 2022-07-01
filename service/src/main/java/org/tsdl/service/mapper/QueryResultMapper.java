package org.tsdl.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tsdl.infrastructure.dto.QueryResultDto;
import org.tsdl.infrastructure.model.QueryResult;

@Mapper
public interface QueryResultMapper {
  @Mapping(target = "type", expression = "java(result.type())")
  QueryResultDto entityToDto(QueryResult result);
}
