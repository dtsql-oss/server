package org.tsdl.client.writer;

import org.tsdl.client.CsvSerializingQueryClientSpecification;
import org.tsdl.client.QueryClientSpecification;
import org.tsdl.client.QueryResultWriter;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.MultipleScalarResult;
import org.tsdl.infrastructure.model.QueryResult;

/**
 * A CSV {@link QueryResultWriter} for {@link MultipleScalarResult} results.
 */
public class ScalarListWriter extends BaseWriter {
  @Override
  public void write(QueryResult result, QueryClientSpecification specification) {
    safeWriteOperation(() -> {
      verifyTypes(result, specification);

      Conditions.checkNotNull(Condition.ARGUMENT, result, "Result must not be null.");
      Conditions.checkNotNull(Condition.ARGUMENT, specification, "Specification must not be null.");

      try (var csvWriter = createWriter(((CsvSerializingQueryClientSpecification) specification).targetFile())) {
        writeDiscriminatorComment(csvWriter, result);

        csvWriter.writeRow("value");
        for (var value : ((MultipleScalarResult) result).values()) {
          csvWriter.writeRow(format(value));
        }

        writeLogs(csvWriter, result.logs());
      }
    });
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
