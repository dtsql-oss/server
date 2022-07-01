package org.tsdl.client;

import org.tsdl.client.reader.DataPointsReader;
import org.tsdl.client.reader.PeriodReader;
import org.tsdl.client.reader.PeriodSetReader;
import org.tsdl.client.reader.ScalarListReader;
import org.tsdl.client.reader.ScalarReader;
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
  public static QueryResultReader getCsvWriter(QueryResultType type) {
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
