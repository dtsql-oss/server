package org.tsdl.implementation.evaluation.impl.connective;

import org.tsdl.implementation.model.connective.AndConnective;
import org.tsdl.implementation.model.filter.SinglePointFilter;
import org.tsdl.infrastructure.model.DataPoint;

import java.util.List;

public record AndConnectiveImpl(List<SinglePointFilter> filters) implements AndConnective {
    @Override
    public List<DataPoint> evaluateFilters(List<DataPoint> data) {
        return data.stream()
          .filter(dp -> filters().stream().allMatch(filter -> filter.evaluate(dp)))
          .toList();
    }
}
