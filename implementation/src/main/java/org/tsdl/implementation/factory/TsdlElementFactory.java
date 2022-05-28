package org.tsdl.implementation.factory;

import org.tsdl.implementation.model.choice.relation.TemporalOperator;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.connective.SinglePointFilterConnective;
import org.tsdl.implementation.model.event.TsdlEvent;
import org.tsdl.implementation.model.filter.NegatedSinglePointFilter;
import org.tsdl.implementation.model.filter.SinglePointFilter;
import org.tsdl.implementation.model.filter.argument.TsdlFilterArgument;
import org.tsdl.implementation.model.sample.TsdlSample;
import org.tsdl.implementation.parsing.enums.AggregatorType;
import org.tsdl.implementation.parsing.enums.ConnectiveIdentifier;
import org.tsdl.implementation.parsing.enums.FilterType;
import org.tsdl.implementation.parsing.enums.TemporalRelationType;

import java.util.List;

public interface TsdlElementFactory {
    TsdlIdentifier getIdentifier(String name);

    SinglePointFilter getFilter(FilterType type, TsdlFilterArgument argument);

    NegatedSinglePointFilter getNegatedFilter(SinglePointFilter filter);

    SinglePointFilterConnective getConnective(ConnectiveIdentifier type, List<SinglePointFilter> filters);

    TsdlFilterArgument getFilterArgument(Double value);

    TsdlFilterArgument getFilterArgument(TsdlSample sample);

    TsdlSample getSample(AggregatorType type, TsdlIdentifier identifier);

    TsdlEvent getEvent(SinglePointFilterConnective definition, TsdlIdentifier identifier);

    TemporalOperator getChoice(TemporalRelationType type, List<TsdlEvent> events);
}
