package org.tsdl.implementation.evaluation.impl.connective;

import java.util.List;
import org.tsdl.implementation.model.connective.OrConnective;
import org.tsdl.implementation.model.filter.SinglePointFilter;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link OrConnective}.
 */
public record OrConnectiveImpl(List<SinglePointFilter> filters) implements OrConnective {
  @Override
  public List<DataPoint> evaluateFilters(List<DataPoint> data) {
    return data.stream()
        .filter(dp -> filters().stream().anyMatch(filter -> filter.evaluate(dp)))
        .toList();
  }
}
