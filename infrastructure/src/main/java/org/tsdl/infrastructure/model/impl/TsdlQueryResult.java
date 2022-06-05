package org.tsdl.infrastructure.model.impl;

import java.util.List;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.infrastructure.model.QueryResult;

public record TsdlQueryResult(List<DataPoint> items) implements QueryResult {
  public TsdlQueryResult {
    Conditions.checkNotNull(Condition.ARGUMENT, items, "Items must not be null.");
  }

  @Override
  public List<DataPoint> getItems() {
    return items();
  }
}
