package org.tsdl.infrastructure.dto;

import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StorageReadDto {
  private Map<String, Object> serviceConfiguration;
  private Map<String, Object> lookupConfiguration;
  private Map<String, Object> transformationConfiguration;
}
