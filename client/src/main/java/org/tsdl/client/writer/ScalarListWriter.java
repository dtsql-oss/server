package org.tsdl.client.writer;

import java.io.IOException;
import org.tsdl.client.CsvSerializingQueryClientSpecification;
import org.tsdl.client.QueryClientSpecification;
import org.tsdl.client.QueryResultWriter;
import org.tsdl.infrastructure.model.MultipleScalarResult;
import org.tsdl.infrastructure.model.QueryResult;

/**
 * A CSV {@link QueryResultWriter} for {@link MultipleScalarResult} results.
 */
public class ScalarListWriter extends BaseWriter<MultipleScalarResult, CsvSerializingQueryClientSpecification> {
  @Override
  protected void writeInternal(MultipleScalarResult result, CsvSerializingQueryClientSpecification specification) throws IOException {
    try (var csvWriter = createWriter(specification.targetFile())) {
      writeDiscriminatorComment(csvWriter, result);

      csvWriter.writeRow("value");
      for (var value : result.values()) {
        csvWriter.writeRow(format(value));
      }

      writeLogs(csvWriter, result.logs());
    }
  }

  @Override
  Class<? extends QueryResult> resultClass() {
    return MultipleScalarResult.class;
  }

  @Override
  Class<? extends QueryClientSpecification> specificationClass() {
    return CsvSerializingQueryClientSpecification.class;
  }
}
