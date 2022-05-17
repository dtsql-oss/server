package org.tsdl.implementation.evaluation.impl.filter;

import org.tsdl.implementation.model.filter.LtFilter;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;

public record LtFilterImpl(Double threshold) implements LtFilter {
    @Override
    public boolean evaluate(DataPoint dataPoint) {
        Conditions.checkNotNull(Condition.STATE, dataPoint, "Data point must not be null.");
        Conditions.checkNotNull(Condition.STATE, dataPoint.getValue(), "Data point value must not be null.");
        Conditions.checkNotNull(Condition.STATE, threshold, "Threshold must not be null.");

        return dataPoint.asDecimal() < threshold;
    }
}
