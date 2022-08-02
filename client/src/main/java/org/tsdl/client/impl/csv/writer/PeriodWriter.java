package org.tsdl.client.impl.csv.writer;

import java.io.IOException;
import org.tsdl.client.api.QueryClientSpecification;
import org.tsdl.client.api.QueryResultWriter;
import org.tsdl.client.impl.csv.CsvSerializingQueryClientSpecification;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.infrastructure.model.TsdlPeriod;

/**
 * A CSV {@link QueryResultWriter} for {@link TsdlPeriod} results.
 */
public class PeriodWriter extends BaseWriter<TsdlPeriod, CsvSerializingQueryClientSpecification> {
  @Override
  protected void writeInternal(TsdlPeriod result, CsvSerializingQueryClientSpecification specification, String targetFile) throws IOException {
    try (var csvWriter = createWriter(targetFile)) {
      writeDiscriminatorComment(csvWriter, result.type());

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
