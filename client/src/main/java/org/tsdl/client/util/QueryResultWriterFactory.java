package org.tsdl.client.util;

import org.tsdl.client.api.QueryResultWriter;
import org.tsdl.client.impl.csv.writer.DataPointsWriter;
import org.tsdl.client.impl.csv.writer.PeriodSetWriter;
import org.tsdl.client.impl.csv.writer.PeriodWriter;
import org.tsdl.client.impl.csv.writer.ScalarListWriter;
import org.tsdl.client.impl.csv.writer.ScalarWriter;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.infrastructure.model.QueryResultType;

/**
 * Provides easy access to {@link QueryResultWriter} instances based on a given {@link QueryResultType}.
 */
public final class QueryResultWriterFactory {
  private QueryResultWriterFactory() {
  }

  /**
   * Constructs a {@link QueryResultWriter} capable of writing a {@link QueryResult} to a CSV file, based on a given {@code type}.
   *
   * @param type the {@link QueryResultType} determining the concrete {@link QueryResultWriter} implementation to be constructed
   */
  public static QueryResultWriter getCsvWriter(QueryResultType type) {
    return switch (type) {
      case DATA_POINTS -> new DataPointsWriter();
      case PERIOD_SET -> new PeriodSetWriter();
      case PERIOD -> new PeriodWriter();
      case SCALAR -> new ScalarWriter();
      case SCALAR_LIST -> new ScalarListWriter();
    };
  }
}
