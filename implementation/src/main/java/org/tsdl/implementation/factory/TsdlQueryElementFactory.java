package org.tsdl.implementation.factory;

import java.time.Instant;
import java.util.List;
import org.tsdl.implementation.math.Calculus;
import org.tsdl.implementation.math.SummaryStatistics;
import org.tsdl.implementation.model.TsdlQuery;
import org.tsdl.implementation.model.choice.relation.TemporalOperand;
import org.tsdl.implementation.model.choice.relation.TemporalOperator;
import org.tsdl.implementation.model.common.ParsableTsdlTimeUnit;
import org.tsdl.implementation.model.common.TsdlDuration;
import org.tsdl.implementation.model.common.TsdlDurationBound;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.connective.SinglePointFilterConnective;
import org.tsdl.implementation.model.event.TsdlEvent;
import org.tsdl.implementation.model.event.definition.ComplexEventFunction;
import org.tsdl.implementation.model.event.definition.EventConnective;
import org.tsdl.implementation.model.event.definition.EventFunction;
import org.tsdl.implementation.model.event.definition.NegatedEventFunction;
import org.tsdl.implementation.model.filter.NegatedSinglePointFilter;
import org.tsdl.implementation.model.filter.SinglePointFilter;
import org.tsdl.implementation.model.filter.argument.TsdlScalarArgument;
import org.tsdl.implementation.model.result.YieldFormat;
import org.tsdl.implementation.model.result.YieldStatement;
import org.tsdl.implementation.model.sample.TsdlSample;
import org.tsdl.implementation.model.sample.aggregation.TsdlAggregator;
import org.tsdl.implementation.model.sample.aggregation.temporal.TimePeriod;
import org.tsdl.implementation.parsing.enums.AggregatorType;
import org.tsdl.implementation.parsing.enums.ConnectiveIdentifier;
import org.tsdl.implementation.parsing.enums.DeviationFilterType;
import org.tsdl.implementation.parsing.enums.TemporalFilterType;
import org.tsdl.implementation.parsing.enums.TemporalRelationType;
import org.tsdl.implementation.parsing.enums.ThresholdFilterType;

/**
 * Factory for creating elements of a {@link TsdlQuery}.
 */
public interface TsdlQueryElementFactory {
  TsdlIdentifier getIdentifier(String name);

  SinglePointFilter getThresholdFilter(ThresholdFilterType type, TsdlScalarArgument argument);

  SinglePointFilter getDeviationFilter(DeviationFilterType type, TsdlScalarArgument reference, TsdlScalarArgument maximumDeviation);

  SinglePointFilter getTemporalFilter(TemporalFilterType type, Instant argument);

  NegatedSinglePointFilter getNegatedFilter(SinglePointFilter filter);

  ComplexEventFunction getConstantEvent(TsdlScalarArgument maximumSlope, TsdlScalarArgument maximumRelativeDeviation);

  ComplexEventFunction getIncreaseEvent(TsdlScalarArgument minimumChange, TsdlScalarArgument maximumChange, TsdlScalarArgument tolerance);

  ComplexEventFunction getDecreaseEvent(TsdlScalarArgument minimumChange, TsdlScalarArgument maximumChange, TsdlScalarArgument tolerance);

  NegatedEventFunction getNegatedEventFunction(EventFunction event);

  SinglePointFilterConnective getFilterConnective(ConnectiveIdentifier type, List<SinglePointFilter> filters);

  EventConnective getEventConnective(ConnectiveIdentifier type, List<EventFunction> events);

  TsdlScalarArgument getScalarArgument(double value);

  TsdlScalarArgument getScalarArgument(TsdlSample sample);

  TsdlSample getSample(TsdlAggregator aggregator, TsdlIdentifier identifier, boolean includeFormatter, String... formatterArgs);

  TsdlAggregator getAggregator(AggregatorType type, Instant lowerBound, Instant upperBound, Calculus calculus);

  TsdlAggregator getAggregator(AggregatorType type, List<TimePeriod> periods, ParsableTsdlTimeUnit unit, SummaryStatistics summaryStatistics);

  TsdlAggregator getAggregator(AggregatorType type, Instant lowerBound, Instant upperBound, SummaryStatistics summaryStatistics);

  TsdlEvent getEvent(EventConnective connective, TsdlIdentifier identifier, TsdlDuration duration);

  TsdlDuration getDuration(TsdlDurationBound lowerBound, TsdlDurationBound upperBound, ParsableTsdlTimeUnit unit);

  TemporalOperator getChoice(TemporalRelationType type, TemporalOperand operand1, TemporalOperand operand2, TsdlDuration tolerance);

  YieldStatement getResult(YieldFormat format, List<TsdlIdentifier> identifier);
}
