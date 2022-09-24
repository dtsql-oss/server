package org.tsdl.implementation.evaluation.impl.connective;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.tsdl.implementation.model.connective.OrFilterConnective;
import org.tsdl.implementation.model.filter.SinglePointFilter;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link OrFilterConnective}.
 */
@Slf4j
public record OrFilterConnectiveImpl(List<SinglePointFilter> filters) implements OrFilterConnective {
  public OrFilterConnectiveImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, filters, "List of filters of 'or' connective must not be null.");
  }

  @Override
  public boolean isSatisfied(DataPoint dp) {
    Conditions.checkNotNull(Condition.ARGUMENT, dp, "Data point to evaluate regarding 'or' connective must not be null.");
    return filters().stream().anyMatch(filter -> filter.evaluate(dp));
  }

  @Override
  public List<DataPoint> evaluateFilters(List<DataPoint> data) {
    Conditions.checkNotNull(Condition.ARGUMENT, data, "List of data points to evaluate 'or' connective over must not be null.");
    log.debug("Evaluating 'or' connective over {} data points.", data.size());

    var filteredList = data.stream()
        .filter(this::isSatisfied)
        .toList();

    log.debug("After evaluating 'or' connective, {} data points are remaining.", filteredList.size());
    return filteredList;
  }
}
