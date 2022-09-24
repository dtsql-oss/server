package org.tsdl.client.impl.csv.writer;

import java.io.IOException;
import org.tsdl.client.api.QueryClientSpecification;
import org.tsdl.client.api.QueryResultWriter;
import org.tsdl.client.impl.csv.CsvSerializingQueryClientSpecification;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.infrastructure.model.TsdlDataPoints;

/**
 * A CSV {@link QueryResultWriter} for {@link TsdlDataPoints} results.
 */
public class DataPointsWriter extends BaseWriter<TsdlDataPoints, CsvSerializingQueryClientSpecification> {
  @Override
  protected void writeInternal(TsdlDataPoints result, CsvSerializingQueryClientSpecification specification, String targetFile) throws IOException {
    try (var csvWriter = createWriter(targetFile)) {
      writeDiscriminatorComment(csvWriter, result.type());

      csvWriter.writeRow("time", "value");
      for (var dp : result.items()) {
        csvWriter.writeRow(dp.timestamp().toString(), dp.asText());
      }

      writeLogs(csvWriter, result.logs());
    }
  }

  @Override
  Class<? extends QueryResult> resultClass() {
    return TsdlDataPoints.class;
  }

  @Override
  Class<? extends QueryClientSpecification> specificationClass() {
    return CsvSerializingQueryClientSpecification.class;
  }
}
