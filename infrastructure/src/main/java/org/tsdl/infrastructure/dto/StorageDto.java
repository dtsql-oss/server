package org.tsdl.infrastructure.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StorageDto {
  @NotNull
  private String name;
  private Map<String, Object> serviceConfiguration;
  private Map<String, Object> lookupConfiguration;
  private Map<String, Object> persistConfiguration;
  private Map<String, Object> transformationConfiguration;
}
