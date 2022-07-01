package org.tsdl.client.util;

import org.tsdl.client.api.QueryResultReader;
import org.tsdl.client.impl.csv.reader.DataPointsReader;
import org.tsdl.client.impl.csv.reader.PeriodReader;
import org.tsdl.client.impl.csv.reader.PeriodSetReader;
import org.tsdl.client.impl.csv.reader.ScalarListReader;
import org.tsdl.client.impl.csv.reader.ScalarReader;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.infrastructure.model.QueryResultType;

/**
 * Provides easy access to {@link QueryResultReader} instances based on a given {@link QueryResultType}.
 */
public final class QueryResultReaderFactory {
  private QueryResultReaderFactory() {
  }

  /**
   * Constructs a {@link QueryResultReader} capable of reading a {@link QueryResult} from a CSV file, based on a given {@code type}.
   *
   * @param type the {@link QueryResultType} determining the concrete {@link QueryResultReader} implementation to be constructed
   */
  public static QueryResultReader getCsvReader(QueryResultType type) {
    switch (type) {
      case DATA_POINTS:
        return new DataPointsReader();
      case PERIOD_SET:
        return new PeriodSetReader();
      case PERIOD:
        return new PeriodReader();
      case SCALAR:
        return new ScalarReader();
      case SCALAR_LIST:
        return new ScalarListReader();
      default:
        throw Conditions.exception(Condition.ARGUMENT, "Unknown query result type '%s'", type);
    }
  }
}
