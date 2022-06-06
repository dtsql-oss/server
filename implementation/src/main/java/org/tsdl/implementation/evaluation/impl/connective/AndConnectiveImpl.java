package org.tsdl.implementation.evaluation.impl.connective;

import java.util.List;
import org.tsdl.implementation.model.connective.AndConnective;
import org.tsdl.implementation.model.filter.SinglePointFilter;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link AndConnective}.
 */
public record AndConnectiveImpl(List<SinglePointFilter> filters) implements AndConnective {

  @Override
  public boolean isSatisfied(DataPoint dp) {
    return filters().stream().allMatch(filter -> filter.evaluate(dp));
  }

  @Override
  public List<DataPoint> evaluateFilters(List<DataPoint> data) {
    return data.stream()
        .filter(this::isSatisfied)
        .toList();
  }
}
