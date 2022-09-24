package org.tsdl.client.impl.csv.reader;

import de.siegmar.fastcsv.reader.CommentStrategy;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.tsdl.client.api.QueryResultReader;
import org.tsdl.client.api.QueryResultWriter;
import org.tsdl.client.util.TsdlClientIoException;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.common.ThrowingConsumer;
import org.tsdl.infrastructure.common.ThrowingSupplier;
import org.tsdl.infrastructure.common.TsdlUtil;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.infrastructure.model.QueryResultType;
import org.tsdl.infrastructure.model.TsdlLogEvent;

/**
 * Encapsulates common functionality for {@link QueryResultReader} implementations.
 */
public abstract class BaseReader<T extends QueryResult> implements QueryResultReader {
  @Override
  public T read(String filePath) {
    return safeReadOperation(() -> readInternal(filePath));
  }

  protected abstract T readInternal(String filePath) throws Exception;

  protected double parseNumber(String str) throws ParseException {
    return TsdlUtil.parseNumber(str);
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

    final var commentStart = "TSDL Query Evaluation Logs";
    var valueRow = iterator.next();
    while (!valueRow.isComment() && !Objects.equals(valueRow.getField(0), commentStart)) {
      rowConsumer.accept(valueRow);
      valueRow = iterator.next();
    }

    Conditions.checkEquals(Condition.STATE, valueRow.getField(0), commentStart, "Expected parser to be at comment section start.");
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
        Conditions.checkIsTrue(Condition.STATE, typeLine.isComment(), "Expected line '%s' to be a comment, but is not.", typeLine.getFields());

        var comment = typeLine.getField(0);
        var type = comment.split("TYPE=")[1];
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
        .commentStrategy(CommentStrategy.READ)
        .build(Path.of(filePath), StandardCharsets.UTF_8);
  }

  protected static <T> T safeReadOperation(ThrowingSupplier<T, Exception> proc) {
    try {
      return proc.get();
    } catch (Exception e) {
      throw new TsdlClientIoException("An error occurred during a read operation.", e);
    }
  }
}
