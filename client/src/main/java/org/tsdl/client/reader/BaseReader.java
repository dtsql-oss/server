package org.tsdl.client.reader;

import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.tsdl.client.QueryResultReader;
import org.tsdl.client.QueryResultWriter;
import org.tsdl.client.TsdlClientException;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.common.ThrowingConsumer;
import org.tsdl.infrastructure.common.ThrowingSupplier;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.infrastructure.model.QueryResultType;
import org.tsdl.infrastructure.model.TsdlLogEvent;

/**
 * Encapsulates common functionality for {@link QueryResultReader} implementations.
 */
public abstract class BaseReader<T extends QueryResult> implements QueryResultReader {
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

  @Override
  public T read(String filePath) {
    return safeReadOperation(() -> readInternal(filePath));
  }

  protected abstract T readInternal(String filePath) throws Exception;

  protected Double parseNumber(String str) throws ParseException {
    return VALUE_FORMATTER.parse(str).doubleValue();
  }

  protected TsdlLogEvent[] parseLogEvents(Iterator<CsvRow> iterator) {
    var logs = new ArrayList<TsdlLogEvent>();
    iterator.forEachRemaining(row -> logs.add(
        TsdlLogEvent.of(
            Instant.parse(row.getField(0)), row.getField(1)
        )
    ));
    return logs.toArray(TsdlLogEvent[]::new);
  }

  protected TsdlLogEvent[] splitCsvRead(CsvReader csvReader, int skipHeaders, ThrowingConsumer<CsvRow, Exception> rowConsumer) throws Exception {
    Conditions.checkIsGreaterThanOrEqual(Condition.ARGUMENT, skipHeaders, 0, "skipHeaders must be greater than or equal to 0; was %s.", skipHeaders);
    var iterator = csvReader.iterator();
    for (var i = 0; i < skipHeaders; i++) {
      iterator.next();
    }

    var valueRow = iterator.next();
    while (!Objects.equals(valueRow.getField(0), "#TSDL Query Evaluation Logs")) {
      rowConsumer.accept(valueRow);
      valueRow = iterator.next();
    }

    Conditions.checkEquals(Condition.STATE, valueRow.getField(0), "#TSDL Query Evaluation Logs", "Expected parser to be at comment section start.");
    iterator.next(); // skip "timestamp;message" row

    return parseLogEvents(iterator);
  }

  /**
   * Peeks into a CSV file written by a CSV {@link QueryResultWriter} and detects the {@link QueryResultType} based on its content.
   *
   * @param filePath the CSV file whose content type should be determined
   * @return the {@link QueryResultType} represented by {@code filePath}.
   */
  public static QueryResultType peekType(String filePath) {
    return safeReadOperation(() -> {
      try (var csvReader = createReader(filePath)) {
        var iterator = csvReader.iterator(); // type is in second line
        iterator.next(); // line 1
        var typeLine = iterator.next(); // line 2 (e.g., #TYPE=SCALAR_LIST)
        var type = typeLine.getField(0).split("=")[1];
        return QueryResultType.valueOf(type);
      }
    });
  }

  @NotNull
  protected static CsvReader createReader(String filePath) throws IOException {
    return CsvReader.builder()
        .fieldSeparator(';')
        .quoteCharacter('"')
        .commentCharacter('#')
        .build(Path.of(filePath), StandardCharsets.UTF_8);
  }

  protected static <T> T safeReadOperation(ThrowingSupplier<T, Exception> proc) {
    try {
      return proc.get();
    } catch (Exception e) {
      throw new TsdlClientException("An error occurred during a read operation.", e);
    }
  }
}
