package org.tsdl.implementation.parsing.impl;


import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.tsdl.grammar.TsdlParser;
import org.tsdl.grammar.TsdlParserBaseListener;
import org.tsdl.implementation.evaluation.impl.TsdlQueryImpl;
import org.tsdl.implementation.factory.TsdlComponentFactory;
import org.tsdl.implementation.factory.TsdlQueryElementFactory;
import org.tsdl.implementation.math.Calculus;
import org.tsdl.implementation.math.SummaryStatistics;
import org.tsdl.implementation.model.TsdlQuery;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.connective.SinglePointFilterConnective;
import org.tsdl.implementation.model.event.EventDuration;
import org.tsdl.implementation.model.event.TsdlEvent;
import org.tsdl.implementation.model.filter.SinglePointFilter;
import org.tsdl.implementation.model.filter.argument.TsdlFilterArgument;
import org.tsdl.implementation.model.filter.argument.TsdlLiteralFilterArgument;
import org.tsdl.implementation.model.result.YieldFormat;
import org.tsdl.implementation.model.result.YieldStatement;
import org.tsdl.implementation.model.sample.TsdlSample;
import org.tsdl.implementation.model.sample.aggregation.TsdlAggregator;
import org.tsdl.implementation.model.sample.aggregation.temporal.TimePeriod;
import org.tsdl.implementation.parsing.TsdlElementParser;
import org.tsdl.implementation.parsing.enums.DeviationFilterType;
import org.tsdl.implementation.parsing.exception.DuplicateIdentifierException;
import org.tsdl.implementation.parsing.exception.InvalidReferenceException;
import org.tsdl.implementation.parsing.exception.TsdlParseException;
import org.tsdl.implementation.parsing.exception.UnknownIdentifierException;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

/**
 * A derivation of {@link TsdlParserBaseListener} used to parse a {@link TsdlQuery} instance from a given string.
 */
public class TsdlListenerImpl extends TsdlParserBaseListener {
  private final TsdlElementParser elementParser = TsdlComponentFactory.INSTANCE.elementParser();
  private final TsdlQueryElementFactory elementFactory = TsdlComponentFactory.INSTANCE.elementFactory();
  private final Calculus calculus = TsdlComponentFactory.INSTANCE.calculus();
  private final Set<TsdlIdentifier> declaredIdentifiers = new HashSet<>();
  private final Map<TsdlIdentifier, TsdlEvent> declaredEvents = new HashMap<>();
  private final Map<TsdlIdentifier, TsdlSample> declaredSamples = new HashMap<>();
  private final TsdlQueryImpl.TsdlQueryImplBuilder queryBuilder = TsdlQueryImpl.builder();

  private final Map<AggregatorBounds, SummaryStatistics> summaryStatisticsByBounds = new HashMap<>();

  @Override
  public void enterIdentifierDeclaration(TsdlParser.IdentifierDeclarationContext ctx) {
    var identifier = parseIdentifier(ctx.IDENTIFIER());

    if (!declaredIdentifiers.contains(identifier)) {
      declaredIdentifiers.add(identifier);
    } else {
      throw new DuplicateIdentifierException(identifier.name());
    }
  }

  @Override
  public void enterFiltersDeclaration(TsdlParser.FiltersDeclarationContext ctx) {
    var connective = parseSinglePointFilterConnective(ctx.filterConnective());
    queryBuilder.filterValue(connective);
  }

  @Override
  public void enterSamplesDeclaration(TsdlParser.SamplesDeclarationContext ctx) {
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
  public void enterEventsDeclaration(TsdlParser.EventsDeclarationContext ctx) {
    var eventList = ctx.eventsDeclarationStatement().eventList();
    var parsedEvents = new ArrayList<TsdlEvent>(); // not reusing declaredEvents.values() because that does not preserve insertion order

    if (eventList.events() != null) {
      var events = eventList.events().eventDeclaration().stream().map(this::parseEvent).toList();
      events.forEach(event -> {
        declaredEvents.put(event.definition().identifier(), event);
        parsedEvents.add(event);
      });
    }

    if (eventList.eventDeclaration() != null) {
      var event = parseEvent(eventList.eventDeclaration());
      declaredEvents.put(event.definition().identifier(), event);
      parsedEvents.add(event);
    }

    queryBuilder.events(parsedEvents);
  }

  @Override
  public void enterChooseDeclaration(TsdlParser.ChooseDeclarationContext ctx) {
    var choiceStatement = ctx.choiceStatement();

    var chosenEvents = new ArrayList<TsdlEvent>();
    for (var identifierDeclaration : choiceStatement.IDENTIFIER()) {
      var identifier = requireIdentifier(identifierDeclaration, IdentifierType.EVENT);
      var referencedEvent = declaredEvents.get(identifier);
      chosenEvents.add(referencedEvent);
    }

    var relationType = elementParser.parseTemporalRelationType(choiceStatement.TEMPORAL_RELATION().getText());
    var operator = elementFactory.getChoice(relationType, chosenEvents);

    queryBuilder.choiceValue(operator);
  }

  @Override
  public void enterYieldDeclaration(TsdlParser.YieldDeclarationContext ctx) {
    var resultFormat = parseResult(ctx.yieldType());
    queryBuilder.result(resultFormat);
  }

  private YieldStatement parseResult(TsdlParser.YieldTypeContext ctx) {
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

  private TsdlSample parseSample(TsdlParser.AggregatorDeclarationContext ctx) {
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
      throw new TsdlParseException("Cannot parse TsdlAggregator, neither 'valueAggregatorDeclaration' nor 'temporalAggregatorDeclaration' rule.");
    }

    return elementFactory.getSample(aggregator, identifier, includeEcho, echoArguments);
  }

  private TsdlAggregator parseValueAggregator(TsdlParser.ValueAggregatorDeclarationContext ctx) {
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

  private TsdlAggregator parseTemporalAggregator(TsdlParser.TemporalAggregatorDeclarationContext ctx) {
    var aggregatorType = elementParser.parseAggregatorType(ctx.TEMPORAL_AGGREGATOR_FUNCTION().getText());
    var timePeriods = parseIntervalList(ctx.intervalList());
    var unit = elementParser.parseEventDurationUnit(ctx.TIME_UNIT().getText());

    return switch (aggregatorType) {
      case TEMPORAL_AVERAGE, TEMPORAL_COUNT, TEMPORAL_MAXIMUM, TEMPORAL_MINIMUM, TEMPORAL_STANDARD_DEVIATION, TEMPORAL_SUM ->
          elementFactory.getAggregator(aggregatorType, timePeriods, unit);
      default -> throw Conditions.exception(Condition.ARGUMENT, "This overload does not support aggregator type '%s'", aggregatorType);
    };
  }

  private List<TimePeriod> parseIntervalList(TsdlParser.IntervalListContext ctx) {
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

  private Instant[] parseTimeRange(TsdlParser.TimeRangeContext ctx) {
    // the empty string literal '""' stands for a non-existing bound, e.g., so that only a lower bound may be specified
    return ctx.STRING_LITERAL().stream()
        .map(literal -> "\"\"".equals(literal.getText()) ? null : elementParser.parseDate(literal.getText(), true))
        .toArray(Instant[]::new);
  }

  private String[] parseEchoArguments(TsdlParser.EchoArgumentListContext ctx) {
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

  private TsdlEvent parseEvent(TsdlParser.EventDeclarationContext ctx) {
    var connective = parseSinglePointFilterConnective(ctx.filterConnective());
    var identifier = parseIdentifier(ctx.identifierDeclaration().IDENTIFIER());
    var duration = parseEventDuration(ctx.durationSpecification());
    return elementFactory.getSinglePointEvent(connective, identifier, duration);
  }

  private SinglePointFilterConnective parseSinglePointFilterConnective(TsdlParser.FilterConnectiveContext ctx) {
    var connectiveIdentifier = elementParser.parseConnectiveIdentifier(ctx.CONNECTIVE_IDENTIFIER().getText());
    var filterList = ctx.singlePointFilterList();

    var parsedFilters = new ArrayList<SinglePointFilter>();
    if (filterList.singlePointFilters() != null) {
      var filtersContext = filterList.singlePointFilters();
      var firstFilters = filtersContext.singlePointFilterDeclaration().stream().map(this::parseSinglePointFilter).toList();
      parsedFilters.addAll(firstFilters);
    }

    if (filterList.singlePointFilterDeclaration() != null) {
      var lastFilter = filterList.singlePointFilterDeclaration();
      parsedFilters.add(parseSinglePointFilter(lastFilter));
    }

    return elementFactory.getConnective(connectiveIdentifier, parsedFilters);
  }

  private SinglePointFilter parseSinglePointFilter(TsdlParser.SinglePointFilterDeclarationContext ctx) {
    SinglePointFilter filter;
    if (ctx.negatedSinglePointFilter() == null) {
      filter = parseSinglePointFilter(ctx.singlePointFilter());
    } else {
      var innerFilter = parseSinglePointFilter(ctx.negatedSinglePointFilter().singlePointFilter());
      filter = elementFactory.getNegatedFilter(innerFilter);
    }

    return filter;
  }

  private SinglePointFilter parseSinglePointFilter(TsdlParser.SinglePointFilterContext ctx) {
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

  private SinglePointFilter parseThresholdFilter(TsdlParser.ThresholdFilterContext ctx) {
    var filterType = elementParser.parseThresholdFilterType(ctx.THRESHOLD_FILTER_TYPE().getText());
    var filterArgument = parseFilterArgument(ctx.filterArgument());

    return elementFactory.getThresholdFilter(filterType, filterArgument);
  }

  private SinglePointFilter parseDeviationFilter(TsdlParser.DeviationFilterContext ctx) {
    var filterType = elementParser.parseDeviationFilterType(
        ctx.DEVIATION_FILTER_TYPE().getText(),
        ctx.deviationFilterArguments().AROUND_FILTER_TYPE().getText()
    );
    var reference = parseFilterArgument(ctx.deviationFilterArguments().filterArgument().get(0));
    var deviation = parseFilterArgument(ctx.deviationFilterArguments().filterArgument().get(1));

    if (deviation instanceof TsdlLiteralFilterArgument deviationArg) {
      var maximumDeviation = deviationArg.value();
      if (filterType == DeviationFilterType.AROUND_RELATIVE && !(maximumDeviation >= 0.0 && maximumDeviation <= 100.0)) {
        throw new TsdlParseException("For relative 'around' filters, the maximum deviation must be between 0 and 100 because it is a percentage.");
      } else if (filterType == DeviationFilterType.AROUND_ABSOLUTE && maximumDeviation < 0) {
        throw new TsdlParseException("For absolute 'around' filters, the maximum deviation must not be less than 0 because it is an absolute value.");
      }
    }

    return elementFactory.getDeviationFilter(filterType, reference, deviation);
  }

  TsdlFilterArgument parseFilterArgument(TsdlParser.FilterArgumentContext ctx) {
    if (ctx.IDENTIFIER() != null) {
      var identifier = requireIdentifier(ctx.IDENTIFIER(), IdentifierType.SAMPLE);
      var referencedSample = declaredSamples.get(identifier);
      return elementFactory.getFilterArgument(referencedSample);
    } else if (ctx.NUMBER() != null) {
      var literalValue = elementParser.parseNumber(ctx.NUMBER().getText());
      return elementFactory.getFilterArgument(literalValue);
    } else {
      throw new TsdlParseException("Cannot parse TsdlFilterArgument, found neither 'identifier' nor 'NUMBER' as 'singlePointFilterArgument'.");
    }
  }

  private SinglePointFilter parseTemporalFilter(TsdlParser.TemporalFilterContext ctx) {
    var filterType = elementParser.parseTemporalFilterType(ctx.TEMPORAL_FILTER_TYPE().getText());
    var filterArgument = elementParser.parseDate(ctx.STRING_LITERAL().getText(), true);
    return elementFactory.getTemporalFilter(filterType, filterArgument);
  }

  private TsdlIdentifier requireIdentifier(TerminalNode node, IdentifierType type) {
    var identifier = parseIdentifier(node);
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

  private EventDuration parseEventDuration(TsdlParser.DurationSpecificationContext ctx) {
    if (ctx == null) {
      return null;
    }

    var bounds = ctx.EVENT_DURATION().getText().substring("FOR".length()).trim().split(",");
    Conditions.checkSizeExactly(Condition.STATE, bounds, 2, "There must be exactly two bounds for an event duration, separated by ','.");

    var lowerBound = elementParser.parseEventDurationBound(bounds[0], TsdlElementParser.DurationBoundType.LOWER_BOUND);
    var upperBound = elementParser.parseEventDurationBound(bounds[1], TsdlElementParser.DurationBoundType.UPPER_BOUND);

    if (lowerBound.value() > upperBound.value()) {
      throw new TsdlParseException("The lower bound of an event duration must be less than or equal to its upper bound.");
    }

    if ((lowerBound.value() == upperBound.value()) && !(lowerBound.inclusive() && upperBound.inclusive())) {
      throw new TsdlParseException("If the lower and upper bound of an event are equal, both of them have to be inclusive, i.e., use '[' and ']'.");
    }

    var unit = elementParser.parseEventDurationUnit(ctx.TIME_UNIT().getText());

    return elementFactory.getEventDuration(lowerBound, upperBound, unit);
  }

  private TsdlIdentifier parseIdentifier(TerminalNode node) {
    return elementFactory.getIdentifier(node.getText());
  }

  public TsdlQuery getQuery() {
    return queryBuilder
        .identifiers(declaredIdentifiers)
        .build();
  }

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
}
