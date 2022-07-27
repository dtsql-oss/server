package org.tsdl.implementation.evaluation.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.tsdl.implementation.evaluation.impl.event.strategy.SinglePointEventStrategyImpl;
import org.tsdl.implementation.model.choice.AnnotatedTsdlPeriod;
import org.tsdl.implementation.model.event.TsdlEvent;
import org.tsdl.implementation.model.event.TsdlEventStrategyType;
import org.tsdl.implementation.model.event.strategy.DurationEventStrategyImpl;
import org.tsdl.implementation.model.event.strategy.TsdlEventStrategy;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link TsdlPeriodAssembler}.
 */
public class TsdlPeriodAssemblerImpl implements TsdlPeriodAssembler {
  @Override
  public List<AnnotatedTsdlPeriod> assemble(List<DataPoint> dataPoints, List<TsdlEvent> events) {
    var detectedPeriods = new ArrayList<AnnotatedTsdlPeriod>();
    var eventsByStrategy = events
        .stream()
        .collect(Collectors.groupingBy(TsdlEvent::computationStrategy));

    // for every strategy (event computation algorithm) that is present in the given query, scan all data points one and
    // detect periods that result from the event definitions exhibiting those strategies
    for (var eventsForStrategy : eventsByStrategy.entrySet()) {
      var strategy = getEventStrategy(eventsForStrategy.getKey());
      var eventDefinitions = eventsForStrategy.getValue().stream().map(TsdlEvent::definition).toList();
      var periodsFromStrategy = strategy.detectPeriods(dataPoints, eventDefinitions);
      detectedPeriods.addAll(periodsFromStrategy);
    }

    return detectedPeriods.stream()
        .sorted(Comparator.comparing(periods -> periods.period().start()))
        .toList();
  }

  private TsdlEventStrategy getEventStrategy(TsdlEventStrategyType type) {
    return switch (type) {
      case SINGLE_POINT_EVENT -> new SinglePointEventStrategyImpl();
      case SINGLE_POINT_EVENT_WITH_DURATION -> new DurationEventStrategyImpl(new SinglePointEventStrategyImpl());
    };
  }
}
