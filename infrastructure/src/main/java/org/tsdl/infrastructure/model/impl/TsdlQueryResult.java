package org.tsdl.infrastructure.model.impl;

import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.infrastructure.model.QueryResult;

import java.util.List;

public record TsdlQueryResult(List<DataPoint> items) implements QueryResult {
    public TsdlQueryResult {
        Conditions.checkNotNull(Condition.ARGUMENT, items, "Items must not be null.");
    }
}
