package org.tsdl.client;

import org.tsdl.client.writer.DataPointsWriter;
import org.tsdl.client.writer.PeriodSetWriter;
import org.tsdl.client.writer.PeriodWriter;
import org.tsdl.client.writer.ScalarListWriter;
import org.tsdl.client.writer.ScalarWriter;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
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
    switch (type) {
      case DATA_POINTS:
        return new DataPointsWriter();
      case PERIOD_SET:
        return new PeriodSetWriter();
      case PERIOD:
        return new PeriodWriter();
      case SCALAR:
        return new ScalarWriter();
      case SCALAR_LIST:
        return new ScalarListWriter();
      default:
        throw Conditions.exception(Condition.ARGUMENT, "Unknown query result type '%s'", type);
    }
  }
}