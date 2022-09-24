package org.tsdl.implementation.parsing.impl;


import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.tsdl.grammar.DtsqlParser;
import org.tsdl.grammar.DtsqlParserBaseListener;
import org.tsdl.grammar.DtsqlParserBaseVisitor;
import org.tsdl.implementation.evaluation.impl.TsdlQueryImpl;
import org.tsdl.implementation.factory.TsdlComponentFactory;
import org.tsdl.implementation.factory.TsdlQueryElementFactory;
import org.tsdl.implementation.math.Calculus;
import org.tsdl.implementation.math.SummaryStatistics;
import org.tsdl.implementation.model.TsdlQuery;
import org.tsdl.implementation.model.choice.relation.TemporalOperand;
import org.tsdl.implementation.model.choice.relation.TemporalOperator;
import org.tsdl.implementation.model.common.TsdlDuration;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.connective.SinglePointFilterConnective;
import org.tsdl.implementation.model.event.TsdlEvent;
import org.tsdl.implementation.model.event.definition.ComplexEventFunction;
import org.tsdl.implementation.model.event.definition.EventConnective;
import org.tsdl.implementation.model.event.definition.EventFunction;
import org.tsdl.implementation.model.filter.SinglePointFilter;
import org.tsdl.implementation.model.filter.argument.TsdlLiteralScalarArgument;
import org.tsdl.implementation.model.filter.argument.TsdlScalarArgument;
import org.tsdl.implementation.model.result.YieldFormat;
import org.tsdl.implementation.model.result.YieldStatement;
import org.tsdl.implementation.model.sample.TsdlSample;
import org.tsdl.implementation.model.sample.aggregation.TsdlAggregator;
import org.tsdl.implementation.model.sample.aggregation.temporal.TimePeriod;
import org.tsdl.implementation.parsing.TsdlElementParser;
import org.tsdl.implementation.parsing.exception.DuplicateIdentifierException;
import org.tsdl.implementation.parsing.exception.InvalidReferenceException;
import org.tsdl.implementation.parsing.exception.TsdlParseException;
import org.tsdl.implementation.parsing.exception.UnknownIdentifierException;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

// TODO: Pattern for parsing lists of any sort could be generified so that it is defined only once

/**
 * A derivation of {@link DtsqlParserBaseListener} used to parse a {@link TsdlQuery} instance from a given string.
 */
public class TsdlListenerImpl extends DtsqlParserBaseListener {
  private enum IdentifierType {
    EVENT, SAMPLE
  }

  /**
   * <p>
   * Different samples may have different lower and upper bounds. Therefore, we may reuse {@link SummaryStatistics} instances only if both lower and
   * upper bound are equal. For instance, all global samples may share the same instance as well as local samples with the same bounds, but local
   * samples with differing bounds may not.
   * </p>
   * <p>
   * This record encapsulates the lower and upper bound of a {@link TsdlAggregator}, enabling support for memorizing {@link SummaryStatistics}
   * instances that may be reused.
   * </p>
   */
  private record AggregatorBounds(Instant lowerBound, Instant upperBound) {
  }

  class TemporalRelationVisitor extends DtsqlParserBaseVisitor<TemporalOperator> {
    @Override
    public TemporalOperator visitEventEvent(DtsqlParser.EventEventContext ctx) {
      var op1 = parseAtomicOperand(ctx.op1);
      var op2 = parseAtomicOperand(ctx.op2);
      var relationType = elementParser.parseTemporalRelationType(ctx.TEMPORAL_RELATION().getText());
      var duration = parseDuration(ctx.timeToleranceSpecification());

      return elementFactory.getChoice(relationType, op1, op2, duration);
    }

    @Override
    public TemporalOperator visitEventRecursive(DtsqlParser.EventRecursiveContext ctx) {
      var op1 = parseAtomicOperand(ctx.op1);
      var op2 = visit(ctx.op2);
      var relationType = elementParser.parseTemporalRelationType(ctx.TEMPORAL_RELATION().getText());
      var duration = parseDuration(ctx.timeToleranceSpecification());

      return elementFactory.getChoice(relationType, op1, op2, duration);
    }

    @Override
    public TemporalOperator visitRecursiveEvent(DtsqlParser.RecursiveEventContext ctx) {
      var op1 = visit(ctx.op1);
      var op2 = parseAtomicOperand(ctx.op2);
      var relationType = elementParser.parseTemporalRelationType(ctx.TEMPORAL_RELATION().getText());
      var duration = parseDuration(ctx.timeToleranceSpecification());

      return elementFactory.getChoice(relationType, op1, op2, duration);
    }

    @Override
    public TemporalOperator visitRecursiveRecursive(DtsqlParser.RecursiveRecursiveContext ctx) {
      var op1 = visit(ctx.op1);
      var op2 = visit(ctx.op2);
      var relationType = elementParser.parseTemporalRelationType(ctx.TEMPORAL_RELATION().getText());
      var duration = parseDuration(ctx.timeToleranceSpecification());

      return elementFactory.getChoice(relationType, op1, op2, duration);
    }

    private TemporalOperand parseAtomicOperand(Token token) {
      var identifier = requireIdentifier(token, IdentifierType.EVENT);
      return declaredEvents.get(identifier);
    }
  }

  private final TsdlElementParser elementParser = TsdlComponentFactory.INSTANCE.elementParser();
  private final TsdlQueryElementFactory elementFactory = TsdlComponentFactory.INSTANCE.elementFactory();
  private final Calculus calculus = TsdlComponentFactory.INSTANCE.calculus();
  private final Set<TsdlIdentifier> declaredIdentifiers = new HashSet<>();
  private final Map<TsdlIdentifier, TsdlEvent> declaredEvents = new HashMap<>();
  private final Map<TsdlIdentifier, TsdlSample> declaredSamples = new HashMap<>();
  private final TsdlQueryImpl.TsdlQueryImplBuilder queryBuilder = TsdlQueryImpl.builder();

  private final Map<AggregatorBounds, SummaryStatistics> summaryStatisticsByBounds = new HashMap<>();

  private final Map<List<TimePeriod>, SummaryStatistics> summaryStatisticsByPeriods = new HashMap<>();

  @Override
  public void enterIdentifierDeclaration(DtsqlParser.IdentifierDeclarationContext ctx) {
    var identifier = parseIdentifier(ctx.IDENTIFIER());

    if (!declaredIdentifiers.contains(identifier)) {
      declaredIdentifiers.add(identifier);
    } else {
      throw new DuplicateIdentifierException(identifier.name());
    }
  }

  @Override
  public void enterFiltersDeclaration(DtsqlParser.FiltersDeclarationContext ctx) {
    var connective = parseSinglePointFilterConnective(ctx.filterConnective());
    queryBuilder.filterValue(connective);
  }

  @Override
  public void enterSamplesDeclaration(DtsqlParser.SamplesDeclarationContext ctx) {
    var aggregatorList = ctx.aggregatorsDeclarationStatement().aggregatorList();
    var parsedSamples = new ArrayList<TsdlSample>(); // not reusing declaredSamples.values() because that does not preserve insertion order

    if (aggregatorList.aggregators() != null) {
      var samples = aggregatorList.aggregators().aggregatorDeclaration().stream().map(this::parseSample).toList();
      samples.forEach(sample -> {
        declaredSamples.put(sample.identifier(), sample);
        parsedSamples.add(sample);
      });
    }

    if (aggregatorList.aggregatorDeclaration() != null) {
      var sample = parseSample(aggregatorList.aggregatorDeclaration());
      declaredSamples.put(sample.identifier(), sample);
      parsedSamples.add(sample);
    }

    queryBuilder.samples(parsedSamples);
  }

  @Override
  public void enterEventsDeclaration(DtsqlParser.EventsDeclarationContext ctx) {
    var eventList = ctx.eventsDeclarationStatement().eventList();
    var parsedEvents = new ArrayList<TsdlEvent>(); // not reusing declaredEvents.values() because that does not preserve insertion order

    if (eventList.events() != null) {
      var events = eventList.events().eventDeclaration().stream().map(this::parseEvent).toList();
      events.forEach(event -> {
        declaredEvents.put(event.identifier(), event);
        parsedEvents.add(event);
      });
    }

    if (eventList.eventDeclaration() != null) {
      var event = parseEvent(eventList.eventDeclaration());
      declaredEvents.put(event.identifier(), event);
      parsedEvents.add(event);
    }

    queryBuilder.events(parsedEvents);
  }

  @Override
  public void enterSelectDeclaration(DtsqlParser.SelectDeclarationContext ctx) {
    var temporalOperator = new TemporalRelationVisitor().visit(ctx.temporalRelation());
    queryBuilder.choiceValue(temporalOperator);
  }

  @Override
  public void enterYieldDeclaration(DtsqlParser.YieldDeclarationContext ctx) {
    var resultFormat = parseResult(ctx.yieldType());
    queryBuilder.result(resultFormat);
  }

  private YieldStatement parseResult(DtsqlParser.YieldTypeContext ctx) {
    var format = elementParser.parseResultFormat(ctx.getText());
    if (format != YieldFormat.SAMPLE && format != YieldFormat.SAMPLE_SET) {
      return elementFactory.getResult(format, null);
    } else if (format == YieldFormat.SAMPLE) {
      var identifier = requireIdentifier(ctx.IDENTIFIER(), IdentifierType.SAMPLE);
      return elementFactory.getResult(format, List.of(identifier));
    } else {
      var identifierList = ctx.identifierList();

      var identifiers = new ArrayList<TsdlIdentifier>();
      if (identifierList.identifiers() != null) {
        var identifiersContext = identifierList.identifiers();
        var parsedIdentifiers = identifiersContext.IDENTIFIER().stream()
            .map(i -> requireIdentifier(i, IdentifierType.SAMPLE))
            .toList();
        identifiers.addAll(parsedIdentifiers);
      }

      if (identifierList.IDENTIFIER() != null) {
        var lastIdentifier = identifierList.IDENTIFIER();
        identifiers.add(requireIdentifier(lastIdentifier, IdentifierType.SAMPLE));
      }

      return elementFactory.getResult(format, identifiers);
    }
  }

  private TsdlSample parseSample(DtsqlParser.AggregatorDeclarationContext ctx) {
    var identifier = parseIdentifier(ctx.identifierDeclaration().IDENTIFIER());
    var includeEcho = ctx.echoStatement() != null;
    var echoArguments = includeEcho && ctx.echoStatement().echoArgumentList() != null
        ? parseEchoArguments(ctx.echoStatement().echoArgumentList())
        : new String[0];

    TsdlAggregator aggregator;
    if (ctx.aggregatorFunctionDeclaration().valueAggregatorDeclaration() != null) {
      aggregator = parseValueAggregator(ctx.aggregatorFunctionDeclaration().valueAggregatorDeclaration());
    } else if (ctx.aggregatorFunctionDeclaration().temporalAggregatorDeclaration() != null) {
      aggregator = parseTemporalAggregator(ctx.aggregatorFunctionDeclaration().temporalAggregatorDeclaration());
    } else {
      throw new TsdlParseException("Cannot parse TsdlAggregator, neither 'valueAggregatorDeclaration' nor 'temporalAggregatorDeclaration' present.");
    }

    return elementFactory.getSample(aggregator, identifier, includeEcho, echoArguments);
  }

  private TsdlAggregator parseValueAggregator(DtsqlParser.ValueAggregatorDeclarationContext ctx) {
    var aggregatorType = elementParser.parseAggregatorType(ctx.VALUE_AGGREGATOR_FUNCTION().getText());

    Instant lowerBound = null;
    Instant upperBound = null;
    if (ctx.timeRange() != null) {
      var timeRange = parseTimeRange(ctx.timeRange());
      Conditions.checkSizeExactly(Condition.STATE, timeRange, 2, "Time range must consist of exactly two timestamps.");

      lowerBound = timeRange[0];
      upperBound = timeRange[1];
      if ((lowerBound != null && upperBound != null) && !lowerBound.isBefore(upperBound)) {
        throw new TsdlParseException("Lower bound of a local sample must be before its upper bound.");
      }
    }

    return switch (aggregatorType) {
      case INTEGRAL -> elementFactory.getAggregator(aggregatorType, lowerBound, upperBound, calculus);
      case AVERAGE, COUNT, MAXIMUM, MINIMUM, STANDARD_DEVIATION, SUM -> {
        var statistics = summaryStatisticsByBounds.computeIfAbsent(
            new AggregatorBounds(lowerBound, upperBound),
            k -> TsdlComponentFactory.INSTANCE.summaryStatistics()
        );
        yield elementFactory.getAggregator(aggregatorType, lowerBound, upperBound, statistics);
      }
      default -> throw Conditions.exception(Condition.ARGUMENT, "This overload does not support aggregator type '%s'", aggregatorType);
    };
  }

  private TsdlAggregator parseTemporalAggregator(DtsqlParser.TemporalAggregatorDeclarationContext ctx) {
    var aggregatorWithUnit = ctx.TEMPORAL_AGGREGATOR_FUNCTION() != null;

    var aggregatorFunction = aggregatorWithUnit
        ? ctx.TEMPORAL_AGGREGATOR_FUNCTION().getText()
        : ctx.UNITLESS_TEMPORAL_AGGREGATOR_FUNCTION().getText();
    var aggregatorType = elementParser.parseAggregatorType(aggregatorFunction);

    var timePeriods = parseIntervalList(ctx.intervalList());

    var unit = aggregatorWithUnit ? elementParser.parseDurationUnit(ctx.TIME_UNIT().getText()) : null;
    Supplier<SummaryStatistics> statisticsSupplier =
        () -> summaryStatisticsByPeriods.computeIfAbsent(timePeriods, k -> TsdlComponentFactory.INSTANCE.summaryStatistics());

    return switch (aggregatorType) {
      case TEMPORAL_AVERAGE, TEMPORAL_MAXIMUM, TEMPORAL_MINIMUM, TEMPORAL_STANDARD_DEVIATION, TEMPORAL_SUM ->
          elementFactory.getAggregator(aggregatorType, timePeriods, unit, statisticsSupplier.get());
      case TEMPORAL_COUNT -> elementFactory.getAggregator(aggregatorType, timePeriods, null, statisticsSupplier.get());
      default -> throw Conditions.exception(Condition.ARGUMENT, "This overload does not support aggregator type '%s'", aggregatorType);
    };
  }

  private List<TimePeriod> parseIntervalList(DtsqlParser.IntervalListContext ctx) {
    var intervalList = new ArrayList<TimePeriod>();

    if (ctx.intervals() != null) {
      var intervals = ctx.intervals().STRING_LITERAL();
      intervals.forEach(timePeriod -> intervalList.add(elementParser.parseTimePeriod(timePeriod.getText())));
    }

    if (ctx.STRING_LITERAL() != null) {
      var lastInterval = ctx.STRING_LITERAL();
      intervalList.add(elementParser.parseTimePeriod(lastInterval.getText()));
    }

    return Collections.unmodifiableList(intervalList);
  }

  private Instant[] parseTimeRange(DtsqlParser.TimeRangeContext ctx) {
    // the empty string literal '""' stands for a non-existing bound, e.g., so that only a lower bound may be specified
    return ctx.STRING_LITERAL().stream()
        .map(literal -> "\"\"".equals(literal.getText()) ? null : elementParser.parseDate(literal.getText(), true))
        .toArray(Instant[]::new);
  }

  private String[] parseEchoArguments(DtsqlParser.EchoArgumentListContext ctx) {
    var arguments = new ArrayList<String>();

    if (ctx.echoArguments() != null) {
      var argList = ctx.echoArguments().echoArgument();
      argList.forEach(arg -> arguments.add(arg.getText()));
    }

    if (ctx.echoArgument() != null) {
      var lastArg = ctx.echoArgument();
      arguments.add(lastArg.getText());
    }

    return arguments.toArray(String[]::new);
  }

  private TsdlEvent parseEvent(DtsqlParser.EventDeclarationContext ctx) {
    var connective = parseEventConnective(ctx.eventConnective());
    var identifier = parseIdentifier(ctx.identifierDeclaration().IDENTIFIER());
    var duration = parseDuration(ctx.durationSpecification());
    return elementFactory.getEvent(connective, identifier, duration);
  }

  private EventConnective parseEventConnective(DtsqlParser.EventConnectiveContext ctx) {
    var connectiveIdentifier = elementParser.parseConnectiveIdentifier(ctx.CONNECTIVE_IDENTIFIER().getText());
    var eventFunctionList = ctx.eventFunctionList();

    var parsedEventFunctions = new ArrayList<EventFunction>();
    if (eventFunctionList.eventFunctions() != null) {
      var firstEventFunctions = eventFunctionList.eventFunctions().eventFunctionDeclaration().stream().map(this::parseEventFunction).toList();
      parsedEventFunctions.addAll(firstEventFunctions);
    }

    if (eventFunctionList.eventFunctionDeclaration() != null) {
      var lastEventFunction = eventFunctionList.eventFunctionDeclaration();
      parsedEventFunctions.add(parseEventFunction(lastEventFunction));
    }

    return elementFactory.getEventConnective(connectiveIdentifier, parsedEventFunctions);
  }

  private SinglePointFilterConnective parseSinglePointFilterConnective(DtsqlParser.FilterConnectiveContext ctx) {
    var connectiveIdentifier = elementParser.parseConnectiveIdentifier(ctx.CONNECTIVE_IDENTIFIER().getText());
    var filterList = ctx.singlePointFilterList();

    var parsedFilters = new ArrayList<SinglePointFilter>();
    if (filterList.singlePointFilters() != null) {
      var firstFilters = filterList.singlePointFilters().singlePointFilterDeclaration().stream().map(this::parseSinglePointFilter).toList();
      parsedFilters.addAll(firstFilters);
    }

    if (filterList.singlePointFilterDeclaration() != null) {
      var lastFilter = filterList.singlePointFilterDeclaration();
      parsedFilters.add(parseSinglePointFilter(lastFilter));
    }

    return elementFactory.getFilterConnective(connectiveIdentifier, parsedFilters);
  }

  private EventFunction parseEventFunction(DtsqlParser.EventFunctionDeclarationContext ctx) {
    if (ctx.singlePointFilterDeclaration() != null) {
      return parseSinglePointFilter(ctx.singlePointFilterDeclaration());
    } else if (ctx.complexEventDeclaration() != null) {
      return parseComplexEventFunction(ctx.complexEventDeclaration());
    } else {
      throw new TsdlParseException("Cannot parse EventFunction, neither 'singlePointFilterDeclaration' nor 'complexEventDeclaration' present.");
    }
  }

  private EventFunction parseComplexEventFunction(DtsqlParser.ComplexEventDeclarationContext ctx) {
    EventFunction event;
    if (ctx.negatedComplexEvent() == null) {
      event = parseComplexEventFunction(ctx.complexEvent());
    } else {
      var innerEvent = parseComplexEventFunction(ctx.negatedComplexEvent().complexEvent());
      event = elementFactory.getNegatedEventFunction(innerEvent);
    }
    return event;
  }

  private ComplexEventFunction parseComplexEventFunction(DtsqlParser.ComplexEventContext ctx) {
    if (ctx.constantEvent() != null) {
      return parseConstantEvent(ctx.constantEvent());
    } else if (ctx.increaseEvent() != null) {
      return parseIncreaseEvent(ctx.increaseEvent());
    } else if (ctx.decreaseEvent() != null) {
      return parseDecreaseEvent(ctx.decreaseEvent());
    } else {
      throw new TsdlParseException("Cannot parse ComplexEventFunction, neither 'constantEvent', 'increaseEvent' nor 'decreaseEvent' present.");
    }
  }

  private ComplexEventFunction parseConstantEvent(DtsqlParser.ConstantEventContext ctx) {
    var maximumSlope = parseScalarArgument(ctx.slope);
    var maximumRelativeDeviation = parseScalarArgument(ctx.deviation);
    return elementFactory.getConstantEvent(maximumSlope, maximumRelativeDeviation);
  }

  private ComplexEventFunction parseIncreaseEvent(DtsqlParser.IncreaseEventContext ctx) {
    var minimumChange = parseScalarArgument(ctx.minChange);
    var maximumChange = parseMonotonicUpperBound(ctx.monotonicUpperBound());
    var tolerance = parseScalarArgument(ctx.tolerance);
    return elementFactory.getIncreaseEvent(minimumChange, maximumChange, tolerance);
  }

  private ComplexEventFunction parseDecreaseEvent(DtsqlParser.DecreaseEventContext ctx) {
    var minimumChange = parseScalarArgument(ctx.minChange);
    var maximumChange = parseMonotonicUpperBound(ctx.monotonicUpperBound());
    var tolerance = parseScalarArgument(ctx.tolerance);
    return elementFactory.getDecreaseEvent(minimumChange, maximumChange, tolerance);
  }

  private SinglePointFilter parseSinglePointFilter(DtsqlParser.SinglePointFilterDeclarationContext ctx) {
    SinglePointFilter filter;
    if (ctx.negatedSinglePointFilter() == null) {
      filter = parseSinglePointFilter(ctx.singlePointFilter());
    } else {
      var innerFilter = parseSinglePointFilter(ctx.negatedSinglePointFilter().singlePointFilter());
      filter = elementFactory.getNegatedFilter(innerFilter);
    }
    return filter;
  }

  private SinglePointFilter parseSinglePointFilter(DtsqlParser.SinglePointFilterContext ctx) {
    if (ctx.thresholdFilter() != null) {
      return parseThresholdFilter(ctx.thresholdFilter());
    } else if (ctx.temporalFilter() != null) {
      return parseTemporalFilter(ctx.temporalFilter());
    } else if (ctx.deviationFilter() != null) {
      return parseDeviationFilter(ctx.deviationFilter());
    } else {
      throw new TsdlParseException("Cannot parse SinglePointFilter, unknown rule - neither 'thresholdFilter' nor 'temporalFilter'.");
    }
  }

  private SinglePointFilter parseThresholdFilter(DtsqlParser.ThresholdFilterContext ctx) {
    var filterType = elementParser.parseThresholdFilterType(ctx.THRESHOLD_FILTER_TYPE().getText());
    var filterArgument = parseScalarArgument(ctx.scalarArgument());
    return elementFactory.getThresholdFilter(filterType, filterArgument);
  }

  private SinglePointFilter parseDeviationFilter(DtsqlParser.DeviationFilterContext ctx) {
    var filterType = elementParser.parseDeviationFilterType(
        ctx.DEVIATION_FILTER_TYPE().getText(),
        ctx.deviationFilterArguments().AROUND_FILTER_TYPE().getText()
    );
    var reference = parseScalarArgument(ctx.deviationFilterArguments().reference);
    var deviation = parseScalarArgument(ctx.deviationFilterArguments().deviation);

    if (deviation instanceof TsdlLiteralScalarArgument deviationArg && deviationArg.value() < 0) {
      throw new TsdlParseException("For 'around' filters, the maximum deviation must not be less than 0 because it is an absolute value.");
    }

    return elementFactory.getDeviationFilter(filterType, reference, deviation);
  }

  TsdlScalarArgument parseScalarArgument(DtsqlParser.ScalarArgumentContext ctx) {
    if (ctx.IDENTIFIER() != null) {
      var identifier = requireIdentifier(ctx.IDENTIFIER(), IdentifierType.SAMPLE);
      var referencedSample = declaredSamples.get(identifier);
      return elementFactory.getScalarArgument(referencedSample);
    } else if (ctx.NUMBER() != null) {
      var literalValue = elementParser.parseNumber(ctx.NUMBER().getText());
      return elementFactory.getScalarArgument(literalValue);
    } else {
      throw new TsdlParseException("Cannot parse TsdlScalarArgument, found neither 'identifier' nor 'NUMBER' as 'singlePointFilterArgument'.");
    }
  }

  TsdlScalarArgument parseMonotonicUpperBound(DtsqlParser.MonotonicUpperBoundContext ctx) {
    if (ctx.HYPHEN() != null) {
      return elementFactory.getScalarArgument(Double.POSITIVE_INFINITY);
    } else if (ctx.scalarArgument() != null) {
      return parseScalarArgument(ctx.scalarArgument());
    } else {
      throw new TsdlParseException("Cannot parse MonotonicUpperbound, found neither 'HYPHEN' nor 'scalarArgument'.");
    }
  }

  private SinglePointFilter parseTemporalFilter(DtsqlParser.TemporalFilterContext ctx) {
    var filterType = elementParser.parseTemporalFilterType(ctx.TEMPORAL_FILTER_TYPE().getText());
    var filterArgument = elementParser.parseDate(ctx.STRING_LITERAL().getText(), true);
    return elementFactory.getTemporalFilter(filterType, filterArgument);
  }

  private TsdlIdentifier requireIdentifier(Token token, IdentifierType type) {
    var identifier = parseIdentifier(token);
    var identifierMap = switch (type) {
      case EVENT -> declaredEvents;
      case SAMPLE -> declaredSamples;
    };

    if (declaredIdentifiers.contains(identifier) && identifierMap.containsKey(identifier)) {
      return identifier;
    } else if (!declaredIdentifiers.contains(identifier)) {
      throw new UnknownIdentifierException(identifier.name(), type.name().toLowerCase());
    } else {
      throw new InvalidReferenceException(identifier.name(), type.name().toLowerCase());
    }
  }

  private TsdlIdentifier requireIdentifier(TerminalNode node, IdentifierType type) {
    return requireIdentifier(node.getSymbol(), type);
  }

  private TsdlDuration parseDuration(DtsqlParser.TimeToleranceSpecificationContext ctx) {
    if (ctx == null) {
      return null;
    }

    return parseDuration(ctx.TIME_TOLERANCE().getText().substring("WITHIN".length()), ctx.TIME_UNIT().getText());
  }

  private TsdlDuration parseDuration(DtsqlParser.DurationSpecificationContext ctx) {
    if (ctx == null) {
      return null;
    }

    return parseDuration(ctx.EVENT_DURATION().getText().substring("FOR".length()), ctx.TIME_UNIT().getText());
  }

  private TsdlDuration parseDuration(String durationSpecificationWithoutPrefix, String timeUnit) {
    var bounds = durationSpecificationWithoutPrefix.trim().split(",");
    Conditions.checkSizeExactly(Condition.STATE, bounds, 2, "There must be exactly two bounds for an event duration, separated by ','.");

    var lowerBound = elementParser.parseDurationBound(bounds[0], TsdlElementParser.DurationBoundType.LOWER_BOUND);
    var upperBound = elementParser.parseDurationBound(bounds[1], TsdlElementParser.DurationBoundType.UPPER_BOUND);

    if (lowerBound.value() > upperBound.value()) {
      throw new TsdlParseException("The lower bound of an event duration must be less than or equal to its upper bound.");
    }

    if ((lowerBound.value() == upperBound.value()) && !(lowerBound.inclusive() && upperBound.inclusive())) {
      throw new TsdlParseException("If the lower and upper bound of an event are equal, both of them have to be inclusive, i.e., use '[' and ']'.");
    }

    var unit = elementParser.parseDurationUnit(timeUnit);

    return elementFactory.getDuration(lowerBound, upperBound, unit);
  }

  private TsdlIdentifier parseIdentifier(Token token) {
    return elementFactory.getIdentifier(token.getText());
  }

  private TsdlIdentifier parseIdentifier(TerminalNode node) {
    return parseIdentifier(node.getSymbol());
  }

  public TsdlQuery getQuery() {
    return queryBuilder
        .identifiers(declaredIdentifiers)
        .build();
  }
}
