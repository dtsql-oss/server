package org.tsdl.implementation.evaluation.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.tsdl.implementation.evaluation.TsdlPeriodAssembler;
import org.tsdl.implementation.evaluation.impl.event.strategy.ConstantEventStrategyImpl;
import org.tsdl.implementation.evaluation.impl.event.strategy.DecreaseEventStrategyImpl;
import org.tsdl.implementation.evaluation.impl.event.strategy.DurationEventStrategyImpl;
import org.tsdl.implementation.evaluation.impl.event.strategy.IncreaseEventStrategyImpl;
import org.tsdl.implementation.evaluation.impl.event.strategy.SinglePointEventStrategyImpl;
import org.tsdl.implementation.model.choice.AnnotatedTsdlPeriod;
import org.tsdl.implementation.model.event.TsdlEvent;
import org.tsdl.implementation.model.event.TsdlEventStrategyType;
import org.tsdl.implementation.model.event.definition.ComplexEventFunction;
import org.tsdl.implementation.model.event.strategy.TsdlEventStrategy;
import org.tsdl.implementation.model.filter.SinglePointFilter;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;

/**
 * Default implementation of {@link TsdlPeriodAssembler}.
 */
@Slf4j
public class TsdlPeriodAssemblerImpl implements TsdlPeriodAssembler {
  @Override
  public List<AnnotatedTsdlPeriod> assemble(List<DataPoint> dataPoints, List<TsdlEvent> events) {
    requirePureEventConnectives(events);

    // TODO FIIIIX
    var detectedPeriods = new ArrayList<AnnotatedTsdlPeriod>();
    var eventsByStrategy = events
        .stream()
        .collect(Collectors.groupingBy(TsdlEvent::computationStrategy));

    // for every strategy (event computation algorithm) that is present in the given query, scan all data points once and
    // detect periods that result from the event definitions exhibiting those strategies
    for (var eventsForStrategy : eventsByStrategy.entrySet()) {
      var strategy = getEventStrategy(eventsForStrategy.getKey());
      var periodsFromStrategy = strategy.detectPeriods(dataPoints, eventsForStrategy.getValue());
      detectedPeriods.addAll(periodsFromStrategy);
    }

    var assembledPeriods = detectedPeriods.stream()
        .sorted(Comparator.comparing(periods -> periods.period().start()))
        .toList();

    if (log.isDebugEnabled()) {
      log.debug("Detected periods: {}.", getDetectedPeriodLogRepresentation(assembledPeriods));
    }

    return assembledPeriods;
  }

  private TsdlEventStrategy getEventStrategy(TsdlEventStrategyType type) {
    return switch (type) {
      case SINGLE_POINT_EVENT -> new SinglePointEventStrategyImpl();
      case SINGLE_POINT_EVENT_WITH_DURATION -> new DurationEventStrategyImpl(new SinglePointEventStrategyImpl());
      case CONSTANT_EVENT -> new ConstantEventStrategyImpl();
      case CONSTANT_EVENT_WITH_DURATION -> new DurationEventStrategyImpl(new ConstantEventStrategyImpl());
      case INCREASE_EVENT -> new IncreaseEventStrategyImpl();
      case INCREASE_EVENT_WITH_DURATION -> new DurationEventStrategyImpl(new IncreaseEventStrategyImpl());
      case DECREASE_EVENT -> new DecreaseEventStrategyImpl();
      case DECREASE_EVENT_WITH_DURATION -> new DurationEventStrategyImpl(new DecreaseEventStrategyImpl());
    };
  }

  private Map<String, List<String>> getDetectedPeriodLogRepresentation(List<AnnotatedTsdlPeriod> periods) {
    return periods.stream()
        .collect(
            Collectors.groupingBy(period -> period.event().name(),
                Collectors.mapping(period -> "%s-%s".formatted(period.period().start(), period.period().end()), Collectors.toList()))
        );
  }

  /**
   * At the moment, it is not valid to mix filter events with complex events or use multiple complex events in one event definition.
   * This method ensures that his constraint is adhered to.
   */
  private void requirePureEventConnectives(List<TsdlEvent> events) {
    for (TsdlEvent event : events) {
      var allFilterEvents = event.connective().events().stream().allMatch(e -> e instanceof SinglePointFilter);
      if (allFilterEvents) {
        continue;
      }

      var allComplexEvents = event.connective().events().stream().allMatch(e -> e instanceof ComplexEventFunction);
      if (allComplexEvents && event.connective().events().size() == 1) {
        continue;
      }

      throw Conditions.exception(Condition.STATE, "There must be either only filter event functions or exactly one complex event function.");
    }
  }
}
