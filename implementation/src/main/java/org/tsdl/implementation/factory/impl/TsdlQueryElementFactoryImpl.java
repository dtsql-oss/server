package org.tsdl.implementation.factory.impl;

import java.time.Instant;
import java.util.List;
import org.tsdl.implementation.evaluation.impl.choice.relation.FollowsOperatorImpl;
import org.tsdl.implementation.evaluation.impl.choice.relation.PrecedesOperatorImpl;
import org.tsdl.implementation.evaluation.impl.common.TsdlDurationImpl;
import org.tsdl.implementation.evaluation.impl.common.TsdlIdentifierImpl;
import org.tsdl.implementation.evaluation.impl.common.formatting.TsdlSampleOutputFormatter;
import org.tsdl.implementation.evaluation.impl.connective.AndConnectiveImpl;
import org.tsdl.implementation.evaluation.impl.connective.OrConnectiveImpl;
import org.tsdl.implementation.evaluation.impl.event.TsdlEventImpl;
import org.tsdl.implementation.evaluation.impl.event.definition.SinglePointEventDefinitionImpl;
import org.tsdl.implementation.evaluation.impl.filter.NegatedSinglePointFilterImpl;
import org.tsdl.implementation.evaluation.impl.filter.argument.TsdlLiteralFilterArgumentImpl;
import org.tsdl.implementation.evaluation.impl.filter.argument.TsdlSampleFilterArgumentImpl;
import org.tsdl.implementation.evaluation.impl.filter.deviation.AbsoluteAroundFilterImpl;
import org.tsdl.implementation.evaluation.impl.filter.deviation.RelativeAroundFilterImpl;
import org.tsdl.implementation.evaluation.impl.filter.temporal.AfterFilterImpl;
import org.tsdl.implementation.evaluation.impl.filter.temporal.BeforeFilterImpl;
import org.tsdl.implementation.evaluation.impl.filter.threshold.GreaterThanFilterImpl;
import org.tsdl.implementation.evaluation.impl.filter.threshold.LowerThanFilterImpl;
import org.tsdl.implementation.evaluation.impl.result.YieldStatementImpl;
import org.tsdl.implementation.evaluation.impl.sample.TsdlSampleImpl;
import org.tsdl.implementation.evaluation.impl.sample.aggregation.temporal.TemporalAverageAggregatorImpl;
import org.tsdl.implementation.evaluation.impl.sample.aggregation.temporal.TemporalCountAggregatorImpl;
import org.tsdl.implementation.evaluation.impl.sample.aggregation.temporal.TemporalMaximumAggregatorImpl;
import org.tsdl.implementation.evaluation.impl.sample.aggregation.temporal.TemporalMinimumAggregatorImpl;
import org.tsdl.implementation.evaluation.impl.sample.aggregation.temporal.TemporalStandardDeviationAggregatorImpl;
import org.tsdl.implementation.evaluation.impl.sample.aggregation.temporal.TemporalSumAggregatorImpl;
import org.tsdl.implementation.evaluation.impl.sample.aggregation.value.AverageAggregatorImpl;
import org.tsdl.implementation.evaluation.impl.sample.aggregation.value.CountAggregatorImpl;
import org.tsdl.implementation.evaluation.impl.sample.aggregation.value.IntegralAggregatorImpl;
import org.tsdl.implementation.evaluation.impl.sample.aggregation.value.MaximumAggregatorImpl;
import org.tsdl.implementation.evaluation.impl.sample.aggregation.value.MinimumAggregatorImpl;
import org.tsdl.implementation.evaluation.impl.sample.aggregation.value.StandardDeviationAggregatorImpl;
import org.tsdl.implementation.evaluation.impl.sample.aggregation.value.SumAggregatorImpl;
import org.tsdl.implementation.factory.TsdlQueryElementFactory;
import org.tsdl.implementation.math.Calculus;
import org.tsdl.implementation.math.SummaryStatistics;
import org.tsdl.implementation.model.choice.relation.TemporalOperator;
import org.tsdl.implementation.model.common.ParsableTsdlTimeUnit;
import org.tsdl.implementation.model.common.TsdlDuration;
import org.tsdl.implementation.model.common.TsdlDurationBound;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.connective.SinglePointFilterConnective;
import org.tsdl.implementation.model.event.TsdlEvent;
import org.tsdl.implementation.model.event.TsdlEventStrategyType;
import org.tsdl.implementation.model.filter.NegatedSinglePointFilter;
import org.tsdl.implementation.model.filter.SinglePointFilter;
import org.tsdl.implementation.model.filter.argument.TsdlFilterArgument;
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
  public SinglePointFilter getDeviationFilter(DeviationFilterType type, TsdlFilterArgument reference, TsdlFilterArgument maximumDeviation) {
    Conditions.checkNotNull(Condition.ARGUMENT, type, "Type of deviation filter must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, reference, "Reference value of deviation filter must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, maximumDeviation, "Maximum deviation of deviation filter must not be null.");
    return switch (type) {
      case AROUND_ABSOLUTE -> new AbsoluteAroundFilterImpl(reference, maximumDeviation);
      case AROUND_RELATIVE -> new RelativeAroundFilterImpl(reference, maximumDeviation);
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
  public TsdlFilterArgument getFilterArgument(double value) {
    Conditions.checkNotNull(Condition.ARGUMENT, value, "Filter argument value must not be null.");
    return new TsdlLiteralFilterArgumentImpl(value);
  }

  @Override
  public TsdlFilterArgument getFilterArgument(TsdlSample sample) {
    Conditions.checkNotNull(Condition.ARGUMENT, sample, "Filter argument sample must not be null.");
    return new TsdlSampleFilterArgumentImpl(sample);
  }

  @Override
  public TsdlSample getSample(TsdlAggregator aggregator, TsdlIdentifier identifier, boolean includeFormatter, String... formatterArgs) {
    Conditions.checkNotNull(Condition.ARGUMENT, identifier, "Identifier for sample must not be null.");
    if (!includeFormatter) {
      Conditions.checkSizeExactly(Condition.ARGUMENT,
          formatterArgs,
          0,
          "If no output formatter is attached to a sample, there must not be any formatting arguments.");
    }

    return new TsdlSampleImpl(
        aggregator,
        identifier,
        includeFormatter ? new TsdlSampleOutputFormatter(formatterArgs) : null
    );
  }

  @Override
  public TsdlAggregator getAggregator(AggregatorType type, Instant lowerBound, Instant upperBound, Calculus calculus) {
    Conditions.checkNotNull(Condition.ARGUMENT, type, "Aggregator type must not be null.");

    if (type == AggregatorType.INTEGRAL) {
      return new IntegralAggregatorImpl(lowerBound, upperBound, calculus);
    }

    throw Conditions.exception(Condition.ARGUMENT, "This overload does not support aggregator type '%s'", type);
  }

  @Override
  public TsdlAggregator getAggregator(AggregatorType type, List<TimePeriod> periods, ParsableTsdlTimeUnit unit, SummaryStatistics summaryStatistics) {
    Conditions.checkNotNull(Condition.ARGUMENT, type, "Aggregator type must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, periods, "Time periods must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, unit, "Time unit must not be null.");

    return switch (type) {
      case TEMPORAL_AVERAGE -> new TemporalAverageAggregatorImpl(periods, unit, summaryStatistics);
      case TEMPORAL_COUNT -> new TemporalCountAggregatorImpl(periods, unit, summaryStatistics);
      case TEMPORAL_MAXIMUM -> new TemporalMaximumAggregatorImpl(periods, unit, summaryStatistics);
      case TEMPORAL_MINIMUM -> new TemporalMinimumAggregatorImpl(periods, unit, summaryStatistics);
      case TEMPORAL_STANDARD_DEVIATION -> new TemporalStandardDeviationAggregatorImpl(periods, unit, summaryStatistics);
      case TEMPORAL_SUM -> new TemporalSumAggregatorImpl(periods, unit, summaryStatistics);
      default -> throw Conditions.exception(Condition.ARGUMENT, "This overload does not support aggregator type '%s'", type);
    };
  }

  @Override
  public TsdlAggregator getAggregator(AggregatorType type, Instant lowerBound, Instant upperBound, SummaryStatistics summaryStatistics) {
    Conditions.checkNotNull(Condition.ARGUMENT, type, "Aggregator type must not be null.");

    return switch (type) {
      case AVERAGE -> new AverageAggregatorImpl(lowerBound, upperBound, summaryStatistics);
      case MAXIMUM -> new MaximumAggregatorImpl(lowerBound, upperBound, summaryStatistics);
      case MINIMUM -> new MinimumAggregatorImpl(lowerBound, upperBound, summaryStatistics);
      case SUM -> new SumAggregatorImpl(lowerBound, upperBound, summaryStatistics);
      case COUNT -> new CountAggregatorImpl(lowerBound, upperBound, summaryStatistics);
      case STANDARD_DEVIATION -> new StandardDeviationAggregatorImpl(lowerBound, upperBound, summaryStatistics);
      default -> throw Conditions.exception(Condition.ARGUMENT, "This overload does not support aggregator type '%s'", type);
    };
  }

  @Override
  public TsdlEvent getSinglePointEvent(SinglePointFilterConnective definition, TsdlIdentifier identifier, TsdlDuration duration) {
    Conditions.checkNotNull(Condition.ARGUMENT, definition, "Filter connective for event must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, identifier, "Identifier for event must not be null.");
    return new TsdlEventImpl(
        new SinglePointEventDefinitionImpl(identifier, definition, duration),
        duration != null ? TsdlEventStrategyType.SINGLE_POINT_EVENT_WITH_DURATION : TsdlEventStrategyType.SINGLE_POINT_EVENT
    );
  }

  @Override
  public TsdlDuration getDuration(TsdlDurationBound lowerBound, TsdlDurationBound upperBound, ParsableTsdlTimeUnit unit) {
    Conditions.checkNotNull(Condition.ARGUMENT, unit, "The unit of the event duration must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, lowerBound, "The lower bound of an event must not be null. Use 0 (inclusive) instead.");
    Conditions.checkNotNull(Condition.ARGUMENT, upperBound, "The upper bound of an event must not be null. Use Long.MAX_VALUE instead.");
    return new TsdlDurationImpl(lowerBound, upperBound, unit);
  }

  @Override
  public TemporalOperator getChoice(TemporalRelationType type, List<TsdlEvent> operands, TsdlDuration tolerance) {
    Conditions.checkNotNull(Condition.ARGUMENT, type, "Type of temporal relation must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, operands, "Operands of temporal relation must not be null.");
    Conditions.checkSizeExactly(Condition.ARGUMENT, operands, 2, "Only binary temporal operators are supported at the moment.");

    return switch (type) {
      case FOLLOWS -> new FollowsOperatorImpl(operands.get(0), operands.get(1), tolerance);
      case PRECEDES -> new PrecedesOperatorImpl(operands.get(0), operands.get(1), tolerance);
    };
  }

  @Override
  public YieldStatement getResult(YieldFormat format, List<TsdlIdentifier> identifiers) {
    Conditions.checkNotNull(Condition.ARGUMENT, format, "Result format must not be null.");
    return new YieldStatementImpl(format, identifiers);
  }
}
