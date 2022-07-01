package org.tsdl.client.impl.csv.reader;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;
import org.tsdl.client.api.QueryResultReader;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.infrastructure.model.TsdlPeriod;

/**
 * A CSV {@link QueryResultReader} for {@link TsdlPeriod} results.
 */
public class PeriodReader extends BaseReader<TsdlPeriod> {
  @Override
  protected TsdlPeriod readInternal(String filePath) throws Exception {
    try (var csvReader = createReader(filePath)) {
      var period = new AtomicReference<TsdlPeriod>();
      var events = splitCsvRead(
          csvReader,
          3,
          row -> {
            Conditions.checkIsTrue(Condition.STATE, period.get() == null, "'PERIOD' CSV file must not contain more than one period.");
            period.set(QueryResult.of(
                Integer.valueOf(row.getField(0)),
                Instant.parse(row.getField(2)),
                Instant.parse(row.getField(3))
                )
            );
          }
      );
      Conditions.checkNotNull(Condition.STATE, period.get(), "Could not extract value.");

      return (TsdlPeriod) period.get().withLogs(events);
    }
  }
}
