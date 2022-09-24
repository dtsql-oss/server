package org.tsdl.client.impl.csv.reader;

import java.time.Instant;
import java.util.ArrayList;
import org.tsdl.client.api.QueryResultReader;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.infrastructure.model.TsdlDataPoints;

/**
 * A CSV {@link QueryResultReader} for {@link TsdlDataPoints} results.
 */
public class DataPointsReader extends BaseReader<TsdlDataPoints> {
  @Override
  protected TsdlDataPoints readInternal(String filePath) throws Exception {
    try (var csvReader = createReader(filePath)) {
      var dataPoints = new ArrayList<DataPoint>();
      var events = splitCsvRead(
          csvReader,
          3,
          row -> dataPoints.add(DataPoint.of(
                  Instant.parse(row.getField(0)),
                  parseNumber(row.getField(1))
              )
          )
      );

      return QueryResult.of(dataPoints, events);
    }
  }
}
