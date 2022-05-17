package org.tsdl.implementation.factory;

import org.tsdl.implementation.model.TsdlQuery;
import org.tsdl.implementation.model.connective.SinglePointFilterConnective;
import org.tsdl.implementation.model.filter.NegatedSinglePointFilter;
import org.tsdl.implementation.model.filter.SinglePointFilter;
import org.tsdl.implementation.parsing.enums.ConnectiveIdentifier;
import org.tsdl.implementation.parsing.enums.FilterType;

import java.util.List;

public interface TsdlElementFactory {
    SinglePointFilter getFilter(FilterType type, Double value);

    NegatedSinglePointFilter getNegatedFilter(SinglePointFilter filter);

    SinglePointFilterConnective getConnective(ConnectiveIdentifier type, List<SinglePointFilter> filters);

    TsdlQuery getQuery(SinglePointFilterConnective connective);
}
