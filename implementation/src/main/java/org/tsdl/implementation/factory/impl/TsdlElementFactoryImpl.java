package org.tsdl.implementation.factory.impl;

import org.tsdl.implementation.evaluation.impl.TsdlQueryImpl;
import org.tsdl.implementation.evaluation.impl.connective.AndConnectiveImpl;
import org.tsdl.implementation.evaluation.impl.connective.OrConnectiveImpl;
import org.tsdl.implementation.evaluation.impl.filter.GtFilterImpl;
import org.tsdl.implementation.evaluation.impl.filter.LtFilterImpl;
import org.tsdl.implementation.evaluation.impl.filter.NegatedSinglePointFilterImpl;
import org.tsdl.implementation.model.TsdlQuery;
import org.tsdl.implementation.model.connective.SinglePointFilterConnective;
import org.tsdl.implementation.model.filter.NegatedSinglePointFilter;
import org.tsdl.implementation.model.filter.SinglePointFilter;
import org.tsdl.implementation.factory.TsdlElementFactory;
import org.tsdl.implementation.parsing.enums.ConnectiveIdentifier;
import org.tsdl.implementation.parsing.enums.FilterType;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

import java.util.List;

public class TsdlElementFactoryImpl implements TsdlElementFactory {
    @Override
    public SinglePointFilter getFilter(FilterType type, Double argument) {
        Conditions.checkNotNull(Condition.ARGUMENT, type, "Type of filter must not be null.");
        Conditions.checkNotNull(Condition.ARGUMENT, argument, "Argument of filter must not be null.");
        return switch (type) {
            case GT -> new GtFilterImpl(argument);
            case LT -> new LtFilterImpl(argument);
        };
    }

    @Override
    public NegatedSinglePointFilter getNegatedFilter(SinglePointFilter filter) {
        Conditions.checkNotNull(Condition.ARGUMENT, filter, "Filter to negate must not be null.");
        return new NegatedSinglePointFilterImpl(filter);
    }

    @Override
    public SinglePointFilterConnective getConnective(ConnectiveIdentifier identifier, List<SinglePointFilter> filters) {
        Conditions.checkNotNull(Condition.ARGUMENT, identifier, "Identifier for connective must not be null.");
        Conditions.checkNotNull(Condition.ARGUMENT, filters, "List of filters for connective must not be null.");
        return switch (identifier) {
            case AND -> new AndConnectiveImpl(filters);
            case OR -> new OrConnectiveImpl(filters);
        };
    }

    @Override
    public TsdlQuery getQuery(SinglePointFilterConnective connective) {
        Conditions.checkNotNull(Condition.ARGUMENT, connective, "Connective to make up query must not be null.");
        return new TsdlQueryImpl(connective);
    }
}
