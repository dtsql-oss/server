package org.tsdl.implementation.factory;

import java.time.Instant;
import java.util.List;
import org.tsdl.implementation.model.TsdlQuery;
import org.tsdl.implementation.model.choice.relation.TemporalOperator;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.connective.SinglePointFilterConnective;
import org.tsdl.implementation.model.event.TsdlEvent;
import org.tsdl.implementation.model.filter.NegatedSinglePointFilter;
import org.tsdl.implementation.model.filter.SinglePointFilter;
import org.tsdl.implementation.model.filter.threshold.argument.TsdlFilterArgument;
import org.tsdl.implementation.model.result.YieldFormat;
import org.tsdl.implementation.model.result.YieldStatement;
import org.tsdl.implementation.model.sample.TsdlSample;
import org.tsdl.implementation.parsing.enums.AggregatorType;
import org.tsdl.implementation.parsing.enums.ConnectiveIdentifier;
import org.tsdl.implementation.parsing.enums.TemporalFilterType;
import org.tsdl.implementation.parsing.enums.TemporalRelationType;
import org.tsdl.implementation.parsing.enums.ThresholdFilterType;

/**
 * Factory for creating elements of a {@link TsdlQuery}.
 */
public interface TsdlElementFactory {
  TsdlIdentifier getIdentifier(String name);

  SinglePointFilter getThresholdFilter(ThresholdFilterType type, TsdlFilterArgument argument);

  SinglePointFilter getTemporalFilter(TemporalFilterType type, Instant argument);

  NegatedSinglePointFilter getNegatedFilter(SinglePointFilter filter);

  SinglePointFilterConnective getConnective(ConnectiveIdentifier type, List<SinglePointFilter> filters);

  TsdlFilterArgument getFilterArgument(Double value);

  TsdlFilterArgument getFilterArgument(TsdlSample sample);

  TsdlSample getSample(AggregatorType type, TsdlIdentifier identifier, boolean includeFormatter, String... formatterArgs);

  TsdlSample getSample(AggregatorType type, Instant lowerBound, Instant upperBound, TsdlIdentifier identifier, boolean includeFormatter,
                       String... formatterArgs);

  TsdlEvent getEvent(SinglePointFilterConnective definition, TsdlIdentifier identifier);

  TemporalOperator getChoice(TemporalRelationType type, List<TsdlEvent> events);

  YieldStatement getResult(YieldFormat format, List<TsdlIdentifier> identifier);
}
