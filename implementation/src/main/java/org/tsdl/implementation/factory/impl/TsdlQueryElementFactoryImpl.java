package org.tsdl.implementation.factory.impl;

import java.time.Instant;
import java.util.List;
import org.tsdl.implementation.evaluation.impl.choice.relation.FollowsOperatorImpl;
import org.tsdl.implementation.evaluation.impl.choice.relation.PrecedesOperatorImpl;
import org.tsdl.implementation.evaluation.impl.common.TsdlIdentifierImpl;
import org.tsdl.implementation.evaluation.impl.common.formatting.TsdlSampleOutputFormatter;
import org.tsdl.implementation.evaluation.impl.connective.AndConnectiveImpl;
import org.tsdl.implementation.evaluation.impl.connective.OrConnectiveImpl;
import org.tsdl.implementation.evaluation.impl.event.EventDurationImpl;
import org.tsdl.implementation.evaluation.impl.event.TsdlEventImpl;
import org.tsdl.implementation.evaluation.impl.event.definition.SinglePointEventDefinitionImpl;
import org.tsdl.implementation.evaluation.impl.filter.NegatedSinglePointFilterImpl;
import org.tsdl.implementation.evaluation.impl.filter.temporal.AfterFilterImpl;
import org.tsdl.implementation.evaluation.impl.filter.temporal.BeforeFilterImpl;
import org.tsdl.implementation.evaluation.impl.filter.threshold.GreaterThanFilterImpl;
import org.tsdl.implementation.evaluation.impl.filter.threshold.LowerThanFilterImpl;
import org.tsdl.implementation.evaluation.impl.filter.threshold.argument.TsdlLiteralFilterArgumentImpl;
import org.tsdl.implementation.evaluation.impl.filter.threshold.argument.TsdlSampleFilterArgumentImpl;
import org.tsdl.implementation.evaluation.impl.result.YieldStatementImpl;
import org.tsdl.implementation.evaluation.impl.sample.TsdlSampleImpl;
import org.tsdl.implementation.evaluation.impl.sample.aggregation.AverageAggregatorImpl;
import org.tsdl.implementation.evaluation.impl.sample.aggregation.CountAggregatorImpl;
import org.tsdl.implementation.evaluation.impl.sample.aggregation.IntegralAggregatorImpl;
import org.tsdl.implementation.evaluation.impl.sample.aggregation.MaximumAggregatorImpl;
import org.tsdl.implementation.evaluation.impl.sample.aggregation.MinimumAggregatorImpl;
import org.tsdl.implementation.evaluation.impl.sample.aggregation.SumAggregatorImpl;
import org.tsdl.implementation.factory.TsdlQueryElementFactory;
import org.tsdl.implementation.model.choice.relation.TemporalOperator;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.connective.SinglePointFilterConnective;
import org.tsdl.implementation.model.event.EventDuration;
import org.tsdl.implementation.model.event.EventDurationBound;
import org.tsdl.implementation.model.event.EventDurationUnit;
import org.tsdl.implementation.model.event.TsdlEvent;
import org.tsdl.implementation.model.event.TsdlEventStrategyType;
import org.tsdl.implementation.model.filter.NegatedSinglePointFilter;
import org.tsdl.implementation.model.filter.SinglePointFilter;
import org.tsdl.implementation.model.filter.threshold.argument.TsdlFilterArgument;
import org.tsdl.implementation.model.result.YieldFormat;
import org.tsdl.implementation.model.result.YieldStatement;
import org.tsdl.implementation.model.sample.TsdlSample;
import org.tsdl.implementation.model.sample.aggregation.TsdlAggregator;
import org.tsdl.implementation.parsing.enums.AggregatorType;
import org.tsdl.implementation.parsing.enums.ConnectiveIdentifier;
import org.tsdl.implementation.parsing.enums.TemporalFilterType;
import org.tsdl.implementation.parsing.enums.TemporalRelationType;
import org.tsdl.implementation.parsing.enums.ThresholdFilterType;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

// TODO Add tests

/**
 * Reference implementation of {@link TsdlQueryElementFactory}.
 */
public class TsdlQueryElementFactoryImpl implements TsdlQueryElementFactory {
  @Override
  public TsdlIdentifier getIdentifier(String name) {
    Conditions.checkNotNull(Condition.ARGUMENT, name, "Identifier name must not be null.");
    return new TsdlIdentifierImpl(name);
  }

  @Override
  public SinglePointFilter getThresholdFilter(ThresholdFilterType type, TsdlFilterArgument argument) {
    Conditions.checkNotNull(Condition.ARGUMENT, type, "Type of threshold filter must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, argument, "Argument of threshold filter must not be null.");
    return switch (type) {
      case GT -> new GreaterThanFilterImpl(argument);
      case LT -> new LowerThanFilterImpl(argument);
    };
  }

  @Override
  public SinglePointFilter getTemporalFilter(TemporalFilterType type, Instant argument) {
    Conditions.checkNotNull(Condition.ARGUMENT, type, "Type of temporal filter must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, argument, "Argument of temporal filter must not be null.");
    return switch (type) {
      case AFTER -> new AfterFilterImpl(argument);
      case BEFORE -> new BeforeFilterImpl(argument);
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
  public TsdlFilterArgument getFilterArgument(Double value) {
    Conditions.checkNotNull(Condition.ARGUMENT, value, "Filter argument value must not be null.");
    return new TsdlLiteralFilterArgumentImpl(value);
  }

  @Override
  public TsdlFilterArgument getFilterArgument(TsdlSample sample) {
    Conditions.checkNotNull(Condition.ARGUMENT, sample, "Filter argument sample must not be null.");
    return new TsdlSampleFilterArgumentImpl(sample);
  }

  @Override
  public TsdlSample getSample(AggregatorType type, Instant lowerBound, Instant upperBound, TsdlIdentifier identifier, boolean includeFormatter,
                              String... formatterArgs) {
    Conditions.checkNotNull(Condition.ARGUMENT, type, "Aggregator type of sample must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, identifier, "Identifier for sample must not be null.");
    if (!includeFormatter) {
      Conditions.checkSizeExactly(Condition.ARGUMENT,
          formatterArgs,
          0,
          "If no output formatter is attached to a sample, there must not be any formatting arguments.");
    }

    var aggregator = getAggregator(type, lowerBound, upperBound);
    return new TsdlSampleImpl(
        aggregator,
        identifier,
        includeFormatter ? new TsdlSampleOutputFormatter(formatterArgs) : null
    );
  }

  @Override
  public TsdlEvent getSinglePointEvent(SinglePointFilterConnective definition, TsdlIdentifier identifier, EventDuration duration) {
    Conditions.checkNotNull(Condition.ARGUMENT, definition, "Filter connective for event must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, identifier, "Identifier for event must not be null.");
    return new TsdlEventImpl(
        new SinglePointEventDefinitionImpl(identifier, definition, duration),
        duration != null ? TsdlEventStrategyType.SINGLE_POINT_EVENT_WITH_DURATION : TsdlEventStrategyType.SINGLE_POINT_EVENT
    );
  }

  @Override
  public EventDuration getEventDuration(EventDurationBound lowerBound, EventDurationBound upperBound, EventDurationUnit unit) {
    Conditions.checkNotNull(Condition.ARGUMENT, unit, "The unit of the event duration must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, lowerBound, "The lower bound of an event must not be null. Use 0 (inclusive) instead.");
    Conditions.checkNotNull(Condition.ARGUMENT, upperBound, "The upper bound of an event must not be null. Use Long.MAX_VALUE instead.");
    return new EventDurationImpl(lowerBound, upperBound, unit);
  }

  @Override
  public TemporalOperator getChoice(TemporalRelationType type, List<TsdlEvent> operands) {
    Conditions.checkNotNull(Condition.ARGUMENT, type, "Type of temporal relation must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, operands, "Operands of temporal relation must not be null.");
    Conditions.checkSizeExactly(Condition.ARGUMENT, operands, 2, "Only binary temporal operators are supported at the moment.");

    return switch (type) {
      case FOLLOWS -> new FollowsOperatorImpl(operands.get(0), operands.get(1));
      case PRECEDES -> new PrecedesOperatorImpl(operands.get(0), operands.get(1));
    };
  }

  @Override
  public YieldStatement getResult(YieldFormat format, List<TsdlIdentifier> identifiers) {
    Conditions.checkNotNull(Condition.ARGUMENT, format, "Result format must not be null.");
    return new YieldStatementImpl(format, identifiers);
  }

  private TsdlAggregator getAggregator(AggregatorType type, Instant lowerBound, Instant upperBound) {
    Conditions.checkNotNull(Condition.ARGUMENT, type, "Aggregator type must not be null.");

    return switch (type) {
      case AVERAGE -> new AverageAggregatorImpl(lowerBound, upperBound);
      case MAXIMUM -> new MaximumAggregatorImpl(lowerBound, upperBound);
      case MINIMUM -> new MinimumAggregatorImpl(lowerBound, upperBound);
      case SUM -> new SumAggregatorImpl(lowerBound, upperBound);
      case COUNT -> new CountAggregatorImpl(lowerBound, upperBound);
      case INTEGRAL -> new IntegralAggregatorImpl(lowerBound, upperBound);
    };
  }
}
