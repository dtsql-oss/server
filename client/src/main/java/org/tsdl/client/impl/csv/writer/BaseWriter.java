package org.tsdl.client.impl.csv.writer;

import de.siegmar.fastcsv.writer.CsvWriter;
import de.siegmar.fastcsv.writer.LineDelimiter;
import de.siegmar.fastcsv.writer.QuoteStrategy;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.tsdl.client.api.QueryClientSpecification;
import org.tsdl.client.api.QueryResultWriter;
import org.tsdl.client.util.TsdlClientException;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.common.ThrowingRunnable;
import org.tsdl.infrastructure.common.TsdlUtil;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.infrastructure.model.QueryResultType;
import org.tsdl.infrastructure.model.TsdlLogEvent;

/**
 * Encapsulates common functionality for {@link QueryResultWriter} implementations.
 */
public abstract class BaseWriter<T extends QueryResult, U extends QueryClientSpecification> implements QueryResultWriter {

  @SuppressWarnings("unchecked") // type erasure - type compatibility is ensured by verifyTypes()
  @Override
  public void write(QueryResult result, QueryClientSpecification specification) {
    safeWriteOperation(() -> {
      verifyTypes(result, specification);
      Conditions.checkNotNull(Condition.ARGUMENT, result, "Result must not be null.");
      Conditions.checkNotNull(Condition.ARGUMENT, specification, "Specification must not be null.");

      writeInternal((T) result, (U) specification);
    });
  }

  protected abstract void writeInternal(T result, U specification) throws IOException;

  protected String formatNumber(Double value) {
    return TsdlUtil.formatNumber(value);
  }

  abstract Class<? extends QueryResult> resultClass();

  abstract Class<? extends QueryClientSpecification> specificationClass();

  private void verifyTypes(QueryResult result, QueryClientSpecification specification) {
    Conditions.checkIsTrue(Condition.ARGUMENT, resultClass().isAssignableFrom(result.getClass()),
        "Result type must be compatible with '%s', but is '%s'.", resultClass().getName(), result.getClass().getName());
    Conditions.checkIsTrue(Condition.ARGUMENT, specificationClass().isAssignableFrom(specification.getClass()),
        "Specification type must be compatible with '%s', but is '%s'.", specificationClass().getName(), specification.getClass().getName());
  }

  @NotNull
  protected static CsvWriter createWriter(String filePath) throws IOException {
    return CsvWriter.builder()
        .fieldSeparator(';')
        .quoteCharacter('"')
        .commentCharacter('#')
        .quoteStrategy(QuoteStrategy.REQUIRED)
        .lineDelimiter(LineDelimiter.PLATFORM)
        .build(Path.of(filePath), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
  }

  protected static void writeDiscriminatorComment(CsvWriter writer, QueryResultType type) {
    writer.writeComment("TSDL Query Result");
    writer.writeComment(String.format("TYPE=%s", type.name()));
  }

  protected void writeLogs(CsvWriter writer, List<TsdlLogEvent> events) {
    writer.writeComment("TSDL Query Evaluation Logs");
    writer.writeRow("timestamp", "message");

    for (var log : events) {
      writer.writeRow(log.dateTime().toString(), log.message());
    }
  }

  protected void safeWriteOperation(ThrowingRunnable<Exception> action) {
    try {
      action.run();
    } catch (Exception e) {
      throw new TsdlClientException("An error occurred during a write operation.", e);
    }
  }
}
