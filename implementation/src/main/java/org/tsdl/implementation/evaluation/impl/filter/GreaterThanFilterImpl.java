package org.tsdl.implementation.evaluation.impl.filter;

import org.tsdl.implementation.model.filter.GreaterThanFilter;
import org.tsdl.implementation.model.filter.argument.TsdlFilterArgument;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;

public record GreaterThanFilterImpl(TsdlFilterArgument threshold) implements GreaterThanFilter {
    @Override
    public boolean evaluate(DataPoint dataPoint) {
        Conditions.checkNotNull(Condition.STATE, dataPoint, "Data point must not be null.");
        Conditions.checkNotNull(Condition.STATE, dataPoint.getValue(), "Data point value must not be null.");
        Conditions.checkNotNull(Condition.STATE, threshold, "Threshold must not be null.");

        return dataPoint.asDecimal() > threshold.value();
    }
}
