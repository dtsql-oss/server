package org.tsdl.implementation.evaluation.impl.connective;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.tsdl.implementation.model.connective.AndConnective;
import org.tsdl.implementation.model.filter.SinglePointFilter;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link AndConnective}.
 */
@Slf4j
public record AndConnectiveImpl(List<SinglePointFilter> filters) implements AndConnective {
  public AndConnectiveImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, filters, "List of filters of 'and' connective must not be null.");
  }

  @Override
  public boolean isSatisfied(DataPoint dp) {
    Conditions.checkNotNull(Condition.ARGUMENT, dp, "Data point to evaluate regarding 'and' connective must not be null.");
    return filters().stream().allMatch(filter -> filter.evaluate(dp));
  }

  @Override
  public List<DataPoint> evaluateFilters(List<DataPoint> data) {
    Conditions.checkNotNull(Condition.ARGUMENT, data, "List of data points to evaluate 'and' connective over must not be null.");
    log.debug("Evaluating 'and' connective over {} data points.", data.size());

    var filteredList = data.stream()
        .filter(this::isSatisfied)
        .toList();

    log.debug("After evaluating 'and' connective, {} data points are remaining.", filteredList.size());
    return filteredList;
  }
}
