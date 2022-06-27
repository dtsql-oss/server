package org.tsdl.infrastructure.dto;

import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tsdl.infrastructure.model.DataPoint;

@Data
@NoArgsConstructor
public class StorageWriteDto {
  private Map<String, Object> serviceConfiguration;
  private Map<String, Object> persistConfiguration;

  @NotNull
  private List<DataPoint> data;
}

