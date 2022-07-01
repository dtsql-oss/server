package org.tsdl.client.writer;

import java.io.IOException;
import org.tsdl.client.CsvSerializingQueryClientSpecification;
import org.tsdl.client.QueryClientSpecification;
import org.tsdl.client.QueryResultWriter;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.infrastructure.model.TsdlDataPoints;

/**
 * A CSV {@link QueryResultWriter} for {@link TsdlDataPoints} results.
 */
public class DataPointsWriter extends BaseWriter<TsdlDataPoints, CsvSerializingQueryClientSpecification> {
  @Override
  protected void writeInternal(TsdlDataPoints result, CsvSerializingQueryClientSpecification specification) throws IOException {
    try (var csvWriter = createWriter(specification.targetFile())) {
      writeDiscriminatorComment(csvWriter, result);

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
