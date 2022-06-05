package org.tsdl.implementation.parsing.impl;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
import org.tsdl.implementation.model.filter.argument.TsdlFilterArgument;
import org.tsdl.implementation.model.sample.TsdlSample;
import org.tsdl.implementation.parsing.TsdlElementParser;
import org.tsdl.implementation.parsing.exception.DuplicateIdentifierException;
import org.tsdl.implementation.parsing.exception.InvalidReferenceException;
import org.tsdl.implementation.parsing.exception.UnknownIdentifierException;

// TODO: use visitor instead of listener?
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
    queryBuilder.filter(connective);
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

    queryBuilder.choice(operator);
  }

  @Override
  public void enterYieldDeclaration(TsdlParser.YieldDeclarationContext ctx) {
    var resultFormat = elementParser.parseResultFormat(ctx.yieldType().getText());
    queryBuilder.result(resultFormat);
  }

  private TsdlSample parseSample(TsdlParser.AggregatorDeclarationContext ctx) {
    var aggregatorType = elementParser.parseAggregatorType(ctx.aggregatorFunctionDeclaration().aggregatorFunction().getText());
    var identifier = parseIdentifier(ctx.identifierDeclaration().identifier());
    return elementFactory.getSample(aggregatorType, identifier);
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
      var innerFilter = parseSinglePointFilter((ctx.negatedSinglePointFilter().singlePointFilter()));
      filter = elementFactory.getNegatedFilter(innerFilter);
    }

    return filter;
  }

  private SinglePointFilter parseSinglePointFilter(TsdlParser.SinglePointFilterContext ctx) {
    var filterType = elementParser.parseFilterType(ctx.filterType().getText());
    TsdlFilterArgument filterArgument;

    if (ctx.singlePointFilterArgument().identifier() != null) {
      var identifier = requireIdentifier(ctx.singlePointFilterArgument().identifier(), IdentifierType.SAMPLE);
      var referencedSample = declaredSamples.get(identifier);
      filterArgument = elementFactory.getFilterArgument(referencedSample);
    } else if (ctx.singlePointFilterArgument().NUMBER() != null) {
      var literalValue = elementParser.parseNumber(ctx.singlePointFilterArgument().NUMBER().getText());
      filterArgument = elementFactory.getFilterArgument(literalValue);
    } else {
      throw new IllegalStateException("Cannot parse SinglePointFilter, found neither 'identifier' nor 'NUMBER' as 'singlePointFilterArgument'");
    }

    return elementFactory.getFilter(filterType, filterArgument);
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
      throw new UnknownIdentifierException(identifier.name());
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

  private enum IdentifierType {EVENT, SAMPLE}
}
