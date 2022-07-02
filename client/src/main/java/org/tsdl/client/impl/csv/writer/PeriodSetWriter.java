package org.tsdl.client.impl.csv.writer;

import java.io.IOException;
import org.tsdl.client.api.QueryClientSpecification;
import org.tsdl.client.api.QueryResultWriter;
import org.tsdl.client.impl.csv.CsvSerializingQueryClientSpecification;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.infrastructure.model.TsdlPeriodSet;

/**
 * A CSV {@link QueryResultWriter} for {@link TsdlPeriodSet} results.
 */
public class PeriodSetWriter extends BaseWriter<TsdlPeriodSet, CsvSerializingQueryClientSpecification> {
  @Override
  protected void writeInternal(TsdlPeriodSet result, CsvSerializingQueryClientSpecification specification) throws IOException {
    try (var csvWriter = createWriter(specification.targetFile())) {
      writeDiscriminatorComment(csvWriter, result.type());

      csvWriter.writeRow("index", "empty", "start", "end");
      for (var periods : result.periods()) {
        csvWriter.writeRow(periods.index().toString(), Boolean.toString(periods.isEmpty()), periods.start().toString(), periods.end().toString());
      }

      writeLogs(csvWriter, result.logs());
    }
  }

  @Override
  Class<? extends QueryResult> resultClass() {
    return TsdlPeriodSet.class;
  }

  @Override
  Class<? extends QueryClientSpecification> specificationClass() {
    return CsvSerializingQueryClientSpecification.class;
  }
}
