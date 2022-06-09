package org.tsdl.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.service.dto.QueryResultDto;

@Mapper
public interface QueryResultMapper {
  @Mapping(target = "type", expression = "java(result.type())")
  QueryResultDto entityToDto(QueryResult result);
}
