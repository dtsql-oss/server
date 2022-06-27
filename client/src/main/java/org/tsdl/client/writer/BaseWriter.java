package org.tsdl.client.writer;

import de.siegmar.fastcsv.writer.CsvWriter;
import de.siegmar.fastcsv.writer.LineDelimiter;
import de.siegmar.fastcsv.writer.QuoteStrategy;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;
import org.tsdl.client.QueryClientSpecification;
import org.tsdl.client.QueryResultWriter;
import org.tsdl.client.TsdlClientException;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.common.ThrowingRunnable;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.infrastructure.model.TsdlLogEvent;

/**
 * Encapsulates common functionality for {@link QueryResultWriter} implementations.
 */
public abstract class BaseWriter implements QueryResultWriter {
  private static final DecimalFormat VALUE_FORMATTER;

  static {
    // Double has a limited precision of 53 bits as per IEEE754, which amounts to roughly 16 decimal digits. Therefore, 16 significant decimal places
    // after a mandatory one should be enough (https://en.wikipedia.org/wiki/Floating-point_arithmetic#IEEE_754:_floating_point_in_modern_computers).
    VALUE_FORMATTER = new DecimalFormat("0.0################");
    var symbols = new DecimalFormatSymbols(Locale.US);
    symbols.setDecimalSeparator('.');
    VALUE_FORMATTER.setDecimalFormatSymbols(symbols);
    VALUE_FORMATTER.setGroupingUsed(false);
  }

  protected String format(Double value) {
    return VALUE_FORMATTER.format(value);
  }

  abstract Class<? extends QueryResult> resultClass();

  abstract Class<? extends QueryClientSpecification> specificationClass();

  protected void verifyTypes(QueryResult result, QueryClientSpecification specification) {
    Conditions.checkIsTrue(Condition.ARGUMENT, resultClass().isAssignableFrom(result.getClass()),
        "Result type must be compatible with '%s', but is '%s'.", resultClass().getName(), result.getClass().getName());
    Conditions.checkIsTrue(Condition.ARGUMENT, specificationClass().isAssignableFrom(specification.getClass()),
        "Specification type must be compatible with '%s', but is '%s'.", specificationClass().getName(), specification.getClass().getName());
  }

  @NotNull
  protected CsvWriter createWriter(String filePath) throws IOException {
    return CsvWriter.builder()
        .fieldSeparator(';')
        .quoteCharacter('"')
        .commentCharacter('#')
        .quoteStrategy(QuoteStrategy.REQUIRED)
        .lineDelimiter(LineDelimiter.PLATFORM)
        .build(Path.of(filePath), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
  }

  protected void writeDiscriminatorComment(CsvWriter writer, QueryResult result) {
    writer.writeComment("TSDL Query Result");
    writer.writeComment("TYPE=%s".formatted(result.type().name()));
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
