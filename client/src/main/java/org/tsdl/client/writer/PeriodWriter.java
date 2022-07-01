package org.tsdl.client.writer;

import java.io.IOException;
import org.tsdl.client.CsvSerializingQueryClientSpecification;
import org.tsdl.client.QueryClientSpecification;
import org.tsdl.client.QueryResultWriter;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.infrastructure.model.TsdlPeriod;

/**
 * A CSV {@link QueryResultWriter} for {@link TsdlPeriod} results.
 */
public class PeriodWriter extends BaseWriter<TsdlPeriod, CsvSerializingQueryClientSpecification> {
  @Override
  protected void writeInternal(TsdlPeriod result, CsvSerializingQueryClientSpecification specification) throws IOException {
    try (var csvWriter = createWriter(specification.targetFile())) {
      writeDiscriminatorComment(csvWriter, result);

      csvWriter.writeRow("index", "empty", "start", "end");
      csvWriter.writeRow(result.index().toString(), Boolean.toString(result.isEmpty()), result.start().toString(), result.end().toString());

      writeLogs(csvWriter, result.logs());
    }
  }

  @Override
  Class<? extends QueryResult> resultClass() {
    return TsdlPeriod.class;
  }

  @Override
  Class<? extends QueryClientSpecification> specificationClass() {
    return CsvSerializingQueryClientSpecification.class;
  }
}
