package org.tsdl.implementation.factory.impl;

import java.util.List;
import org.tsdl.implementation.evaluation.impl.choice.relation.FollowsOperatorImpl;
import org.tsdl.implementation.evaluation.impl.choice.relation.PrecedesOperatorImpl;
import org.tsdl.implementation.evaluation.impl.common.TsdlIdentifierImpl;
import org.tsdl.implementation.evaluation.impl.connective.AndConnectiveImpl;
import org.tsdl.implementation.evaluation.impl.connective.OrConnectiveImpl;
import org.tsdl.implementation.evaluation.impl.event.TsdlEventImpl;
import org.tsdl.implementation.evaluation.impl.filter.GreaterThanFilterImpl;
import org.tsdl.implementation.evaluation.impl.filter.LowerThanFilterImpl;
import org.tsdl.implementation.evaluation.impl.filter.NegatedSinglePointFilterImpl;
import org.tsdl.implementation.evaluation.impl.filter.argument.TsdlLiteralFilterArgumentImpl;
import org.tsdl.implementation.evaluation.impl.filter.argument.TsdlSampleFilterArgumentImpl;
import org.tsdl.implementation.evaluation.impl.sample.TsdlSampleImpl;
import org.tsdl.implementation.evaluation.impl.sample.aggregation.AverageAggregatorImpl;
import org.tsdl.implementation.evaluation.impl.sample.aggregation.MaximumAggregatorImpl;
import org.tsdl.implementation.evaluation.impl.sample.aggregation.MinimumAggregatorImpl;
import org.tsdl.implementation.evaluation.impl.sample.aggregation.SumAggregatorImpl;
import org.tsdl.implementation.factory.TsdlElementFactory;
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
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

public class TsdlElementFactoryImpl implements TsdlElementFactory {
  @Override
  public TsdlIdentifier getIdentifier(String name) {
    Conditions.checkNotNull(Condition.ARGUMENT, name, "Identifier name must not be null.");
    return new TsdlIdentifierImpl(name);
  }

  @Override
  public SinglePointFilter getFilter(FilterType type, TsdlFilterArgument argument) {
    Conditions.checkNotNull(Condition.ARGUMENT, type, "Type of filter must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, argument, "Argument of filter must not be null.");
    return switch (type) {
      case GT -> new GreaterThanFilterImpl(argument);
      case LT -> new LowerThanFilterImpl(argument);
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
  public TsdlSample getSample(AggregatorType type, TsdlIdentifier identifier) {
    Conditions.checkNotNull(Condition.ARGUMENT, type, "Aggregator type of sample must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, identifier, "Identifier for sample must not be null.");

    var aggregator = switch (type) {
      case AVERAGE -> new AverageAggregatorImpl();
      case MAXIMUM -> new MaximumAggregatorImpl();
      case MINIMUM -> new MinimumAggregatorImpl();
      case SUM -> new SumAggregatorImpl();
    };

    return new TsdlSampleImpl(aggregator, identifier);
  }

  @Override
  public TsdlEvent getEvent(SinglePointFilterConnective definition, TsdlIdentifier identifier) {
    Conditions.checkNotNull(Condition.ARGUMENT, definition, "Filter connective for event must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, identifier, "Identifier for event must not be null.");
    return new TsdlEventImpl(definition, identifier);
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
}
