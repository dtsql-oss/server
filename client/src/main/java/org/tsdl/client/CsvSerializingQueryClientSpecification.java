package org.tsdl.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.tsdl.infrastructure.dto.QueryDto;

/**
 * A {@link QueryClientSpecification} specific to {@link CsvSerializingTsdlClient}.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(fluent = true)
public class CsvSerializingQueryClientSpecification implements QueryClientSpecification {
  private QueryDto query;

  private String serverUrl;

  private String targetFile;
}
