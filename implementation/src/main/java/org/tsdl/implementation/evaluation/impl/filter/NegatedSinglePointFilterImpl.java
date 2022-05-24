package org.tsdl.implementation.evaluation.impl.filter;

import org.tsdl.implementation.model.filter.NegatedSinglePointFilter;
import org.tsdl.implementation.model.filter.SinglePointFilter;
import org.tsdl.infrastructure.model.DataPoint;

public record NegatedSinglePointFilterImpl(SinglePointFilter filter) implements NegatedSinglePointFilter {
    @Override
    public boolean evaluate(DataPoint dataPoint) {
        return !filter.evaluate(dataPoint);
    }
}
