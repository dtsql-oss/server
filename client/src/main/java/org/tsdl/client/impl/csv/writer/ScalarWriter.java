package org.tsdl.client.impl.csv.writer;

import java.io.IOException;
import org.tsdl.client.api.QueryClientSpecification;
import org.tsdl.client.api.QueryResultWriter;
import org.tsdl.client.impl.csv.CsvSerializingQueryClientSpecification;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.infrastructure.model.SingularScalarResult;

/**
 * A CSV {@link QueryResultWriter} for {@link SingularScalarResult} results.
 */
public class ScalarWriter extends BaseWriter<SingularScalarResult, CsvSerializingQueryClientSpecification> {
  @Override
  protected void writeInternal(SingularScalarResult result, CsvSerializingQueryClientSpecification specification) throws IOException {
    try (var csvWriter = createWriter(specification.targetFile())) {
      writeDiscriminatorComment(csvWriter, result);

      csvWriter.writeRow("value");
      csvWriter.writeRow(format(result.value()));

      writeLogs(csvWriter, result.logs());
    }
  }

  @Override
  Class<? extends QueryResult> resultClass() {
    return SingularScalarResult.class;
  }

  @Override
  Class<? extends QueryClientSpecification> specificationClass() {
    return CsvSerializingQueryClientSpecification.class;
  }
}
