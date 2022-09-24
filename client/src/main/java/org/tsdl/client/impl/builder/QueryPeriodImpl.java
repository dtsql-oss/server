package org.tsdl.client.impl.builder;

import java.time.Instant;
import org.tsdl.client.api.builder.QueryPeriod;

/**
 * Default implementation of {@link QueryPeriod}.
 */
public class QueryPeriodImpl implements QueryPeriod {
  private final Instant start;
  private final Instant end;

  private QueryPeriodImpl(Instant start, Instant end) {
    this.start = start;
    this.end = end;
  }

  @Override
  public Instant start() {
    return start;
  }

  @Override
  public Instant end() {
    return end;
  }

  public static QueryPeriod period(Instant start, Instant end) {
    return new QueryPeriodImpl(start, end);
  }

  public static QueryPeriod period(String start, String end) {
    return new QueryPeriodImpl(BuilderUtil.requireInstant(start), BuilderUtil.requireInstant(end));
  }
}
