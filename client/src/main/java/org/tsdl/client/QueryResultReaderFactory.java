package org.tsdl.client;

import org.tsdl.client.reader.DataPointsReader;
import org.tsdl.client.reader.PeriodReader;
import org.tsdl.client.reader.PeriodSetReader;
import org.tsdl.client.reader.ScalarListReader;
import org.tsdl.client.reader.ScalarReader;
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
    return switch (type) {
      case DATA_POINTS -> new DataPointsReader();
      case PERIOD_SET -> new PeriodSetReader();
      case PERIOD -> new PeriodReader();
      case SCALAR -> new ScalarReader();
      case SCALAR_LIST -> new ScalarListReader();
    };
  }
}
