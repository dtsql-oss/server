package org.tsdl.implementation.parsing.impl;


import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.tsdl.grammar.TsdlParser;
import org.tsdl.grammar.TsdlParserBaseListener;
import org.tsdl.implementation.evaluation.impl.TsdlQueryImpl;
import org.tsdl.implementation.factory.ObjectFactory;
import org.tsdl.implementation.factory.TsdlElementFactory;
import org.tsdl.implementation.model.TsdlQuery;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.connective.SinglePointFilterConnective;
import org.tsdl.implementation.model.event.TsdlEvent;
import org.tsdl.implementation.model.filter.SinglePointFilter;
import org.tsdl.implementation.model.filter.threshold.argument.TsdlFilterArgument;
import org.tsdl.implementation.model.result.YieldFormat;
import org.tsdl.implementation.model.result.YieldStatement;
import org.tsdl.implementation.model.sample.TsdlSample;
import org.tsdl.implementation.parsing.TsdlElementParser;
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
  private final TsdlElementParser elementParser = ObjectFactory.INSTANCE.elementParser();
  private final TsdlElementFactory elementFactory = ObjectFactory.INSTANCE.elementFactory();
  private final Set<TsdlIdentifier> declaredIdentifiers = new HashSet<>();
  private final Map<TsdlIdentifier, TsdlEvent> declaredEvents = new HashMap<>();
  private final Map<TsdlIdentifier, TsdlSample> declaredSamples = new HashMap<>();
  private final TsdlQueryImpl.TsdlQueryImplBuilder queryBuilder = TsdlQueryImpl.builder();

  @Override
  public void enterIdentifierDeclaration(TsdlParser.IdentifierDeclarationContext ctx) {
    var identifier = parseIdentifier(ctx.identifier());

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
  public void enterChooseDeclaration(TsdlParser.ChooseDeclarationContext ctx) {
    var choiceStatement = ctx.choiceStatement();

    var chosenEvents = new ArrayList<TsdlEvent>();
    for (var identifierDeclaration : choiceStatement.identifier()) {
      var identifier = requireIdentifier(identifierDeclaration, IdentifierType.EVENT);
      var referencedEvent = declaredEvents.get(identifier);
      chosenEvents.add(referencedEvent);
    }

    var relationType = elementParser.parseTemporalRelationType(choiceStatement.temporalRelation().getText());
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
      var identifier = requireIdentifier(ctx.identifier(), IdentifierType.SAMPLE);
      return elementFactory.getResult(format, List.of(identifier));
    } else {
      var identifierList = ctx.identifierList();

      var identifiers = new ArrayList<TsdlIdentifier>();
      if (identifierList.identifiers() != null) {
        var identifiersContext = identifierList.identifiers();
        var parsedIdentifiers = identifiersContext.identifier().stream()
            .map(i -> requireIdentifier(i, IdentifierType.SAMPLE))
            .toList();
        identifiers.addAll(parsedIdentifiers);
      }

      if (identifierList.identifier() != null) {
        var lastIdentifier = identifierList.identifier();
        identifiers.add(requireIdentifier(lastIdentifier, IdentifierType.SAMPLE));
      }

      return elementFactory.getResult(format, identifiers);
    }
  }

  private TsdlSample parseSample(TsdlParser.AggregatorDeclarationContext ctx) {
    var aggregatorType = elementParser.parseAggregatorType(ctx.aggregatorFunctionDeclaration().aggregatorFunction().getText());
    var identifier = parseIdentifier(ctx.identifierDeclaration().identifier());
    var includeEcho = ctx.echoStatement() != null;
    var echoArguments = includeEcho && ctx.echoStatement().echoArgumentList() != null
        ? parseEchoArguments(ctx.echoStatement().echoArgumentList())
        : new String[0];

    var global = ctx.aggregatorFunctionDeclaration().aggregatorInput().INPUT_VARIABLE() != null;
    if (global) {
      return elementFactory.getSample(aggregatorType, identifier, includeEcho, echoArguments);
    } else {
      var timeRange = parseTimeRange(ctx.aggregatorFunctionDeclaration().aggregatorInput().timeRange());
      Conditions.checkSizeExactly(Condition.STATE, timeRange, 2, "Time range must consist of exactly two timestamps.");

      var lowerBound = timeRange[0];
      var upperBound = timeRange[1];
      if (!lowerBound.isBefore(upperBound)) {
        throw new TsdlParseException("Lower bound of a local sample must be before its upper bound.");
      }

      return elementFactory.getSample(aggregatorType, lowerBound, upperBound, identifier, includeEcho, echoArguments);
    }
  }

  private Instant[] parseTimeRange(TsdlParser.TimeRangeContext ctx) {
    return ctx.STRING_LITERAL().stream()
        .map(literal -> elementParser.parseDateLiteral(literal.getText()))
        .toArray(Instant[]::new);
  }

  private String[] parseEchoArguments(TsdlParser.EchoArgumentListContext ctx) {
    var arguments = new ArrayList<String>();

    if (ctx.echoArguments() != null) {
      var argList = ctx.echoArguments().echoArgumentDeclaration();
      argList.forEach(arg -> arguments.add(arg.echoArgument().getText()));
    }

    if (ctx.echoArgumentDeclaration() != null) {
      var lastArg = ctx.echoArgumentDeclaration().echoArgument();
      arguments.add(lastArg.getText());
    }

    return arguments.toArray(String[]::new);
  }

  private TsdlEvent parseEvent(TsdlParser.EventDeclarationContext ctx) {
    var connective = parseSinglePointFilterConnective(ctx.filterConnective());
    var identifier = parseIdentifier(ctx.identifierDeclaration().identifier());
    return elementFactory.getEvent(connective, identifier);
  }

  private SinglePointFilterConnective parseSinglePointFilterConnective(TsdlParser.FilterConnectiveContext ctx) {
    var connectiveIdentifier = elementParser.parseConnectiveIdentifier(ctx.connectiveIdentifier().getText());
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
    if (ctx.valueThresholdFilter() != null) {
      return parseThresholdFilter(ctx.valueThresholdFilter());
    } else if (ctx.temporalFilter() != null) {
      return parseTemporalFilter(ctx.temporalFilter());
    } else {
      throw new TsdlParseException("Cannot parse SinglePointFilter, unknown rule - neither 'valueThresholdFilter' nor 'temporalFilter'.");
    }
  }

  private SinglePointFilter parseThresholdFilter(TsdlParser.ValueThresholdFilterContext ctx) {
    var filterType = elementParser.parseThresholdFilterType(ctx.valueTresholdFilterType().getText());
    TsdlFilterArgument filterArgument;

    if (ctx.valueThresholdFilterArgument().identifier() != null) {
      var identifier = requireIdentifier(ctx.valueThresholdFilterArgument().identifier(), IdentifierType.SAMPLE);
      var referencedSample = declaredSamples.get(identifier);
      filterArgument = elementFactory.getFilterArgument(referencedSample);
    } else if (ctx.valueThresholdFilterArgument().NUMBER() != null) {
      var literalValue = elementParser.parseNumber(ctx.valueThresholdFilterArgument().NUMBER().getText());
      filterArgument = elementFactory.getFilterArgument(literalValue);
    } else {
      throw new TsdlParseException("Cannot parse SinglePointFilter, found neither 'identifier' nor 'NUMBER' as 'singlePointFilterArgument'.");
    }

    return elementFactory.getThresholdFilter(filterType, filterArgument);
  }

  private SinglePointFilter parseTemporalFilter(TsdlParser.TemporalFilterContext ctx) {
    var filterType = elementParser.parseTemporalFilterType(ctx.temporalFilterType().getText());
    var filterArgument = elementParser.parseDateLiteral(ctx.temporalFilterArgument().getText());
    return elementFactory.getTemporalFilter(filterType, filterArgument);
  }

  private TsdlIdentifier requireIdentifier(TsdlParser.IdentifierContext ctx, IdentifierType type) {
    var identifier = parseIdentifier(ctx);
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

  private TsdlIdentifier parseIdentifier(TsdlParser.IdentifierContext ctx) {
    var identifierName = ctx.IDENTIFIER().getText();
    return elementFactory.getIdentifier(identifierName);
  }

  public TsdlQuery getQuery() {
    return queryBuilder
        .identifiers(declaredIdentifiers)
        .build();
  }

  private enum IdentifierType {
    EVENT, SAMPLE
  }
}
