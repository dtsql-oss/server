package org.tsdl.client.reader;

import java.time.Instant;
import java.util.ArrayList;
import org.tsdl.client.QueryResultReader;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.infrastructure.model.TsdlPeriod;
import org.tsdl.infrastructure.model.TsdlPeriodSet;

/**
 * A CSV {@link QueryResultReader} for {@link TsdlPeriodSet} results.
 */
public class PeriodSetReader extends BaseReader<TsdlPeriodSet> {
  @Override
  protected TsdlPeriodSet readInternal(String filePath) throws Exception {
    try (var csvReader = createReader(filePath)) {
      var periods = new ArrayList<TsdlPeriod>();
      var events = splitCsvRead(
          csvReader,
          3,
          row -> {
            periods.add(QueryResult.of(
                Integer.valueOf(row.getField(0)),
                Instant.parse(row.getField(2)),
                Instant.parse(row.getField(3))
            ));
          }
      );

      return QueryResult.of(periods.size(), periods, events);
    }
  }
}
