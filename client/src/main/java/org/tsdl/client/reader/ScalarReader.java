package org.tsdl.client.reader;

import java.util.concurrent.atomic.AtomicReference;
import org.tsdl.client.QueryResultReader;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.infrastructure.model.SingularScalarResult;

/**
 * A CSV {@link QueryResultReader} for {@link SingularScalarResult} results.
 */
public class ScalarReader extends BaseReader<SingularScalarResult> {
  @Override
  protected SingularScalarResult readInternal(String filePath) throws Exception {
    try (var csvReader = createReader(filePath)) {
      var value = new AtomicReference<Double>();
      var events = splitCsvRead(
          csvReader,
          3,
          row -> {
            Conditions.checkIsTrue(Condition.STATE, value.get() == null, "'SCALAR' CSV file must not contain more than one value.");
            value.set(parseNumber(row.getField(0)));
          }
      );
      Conditions.checkNotNull(Condition.STATE, value.get(), "Could not extract value.");

      return QueryResult.of(value.get(), events);
    }
  }
}
