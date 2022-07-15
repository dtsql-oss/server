package org.tsdl.client.impl.csv.reader;

import java.util.ArrayList;
import org.tsdl.client.api.QueryResultReader;
import org.tsdl.infrastructure.model.MultipleScalarResult;
import org.tsdl.infrastructure.model.QueryResult;

/**
 * A CSV {@link QueryResultReader} for {@link org.tsdl.infrastructure.model.MultipleScalarResult} results.
 */
public class ScalarListReader extends BaseReader<MultipleScalarResult> {
  @Override
  protected MultipleScalarResult readInternal(String filePath) throws Exception {
    try (var csvReader = createReader(filePath)) {
      var valueList = new ArrayList<Double>();
      var events = splitCsvRead(
          csvReader,
          3,
          row -> {
            var value = parseNumber(row.getField(0));
            valueList.add(value);
          }
      );

      var values = valueList.toArray(Double[]::new);
      return QueryResult.of(values, events);
    }
  }
}
