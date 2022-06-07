package org.tsdl.implementation.evaluation.impl.choice.relation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.tsdl.implementation.model.choice.AnnotatedTsdlPeriod;
import org.tsdl.implementation.model.choice.relation.PrecedesOperator;
import org.tsdl.implementation.model.event.TsdlEvent;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.infrastructure.model.TsdlPeriod;
import org.tsdl.infrastructure.model.TsdlPeriods;

/**
 * Default implementation of {@link PrecedesOperator}.
 */
@Slf4j
public record PrecedesOperatorImpl(TsdlEvent operand1, TsdlEvent operand2) implements PrecedesOperator {
  @Override
  public TsdlPeriods evaluate(List<AnnotatedTsdlPeriod> periods) {
    log.debug("Evaluating 'precedes' temporal operator.");
    Conditions.checkNotNull(Condition.ARGUMENT, periods, "Annotated periods as input to 'precedes' operator must not be null.");
    Conditions.checkNotNull(Condition.STATE, operand1, "First event argument of 'precedes' operator must not be null.");
    Conditions.checkNotNull(Condition.STATE, operand2, "Second event argument of 'precedes' operator must not be null.");

    if (periods.size() < 2) {
      log.debug("Less than two annotated periods as input, hence resulting set of 'precedes' periods is empty.");
      return QueryResult.of(0, List.of());
    }

    var chosenPeriods = new ArrayList<TsdlPeriod>();
    for (var i = 1; i < periods.size(); i++) {
      var previousPeriod = periods.get(i - 1);
      var currentPeriod = periods.get(i);

      if (precedes(previousPeriod, currentPeriod)) {
        var newPeriod = mergePeriods(previousPeriod, currentPeriod, chosenPeriods.size());
        chosenPeriods.add(newPeriod);
      }
    }

    return QueryResult.of(chosenPeriods.size(), chosenPeriods);
  }

  private boolean precedes(AnnotatedTsdlPeriod previousPeriod, AnnotatedTsdlPeriod currentPeriod) {
    return Objects.equals(operand1().identifier(), previousPeriod.event())
        && Objects.equals(operand2().identifier(), currentPeriod.event());
  }

  private TsdlPeriod mergePeriods(AnnotatedTsdlPeriod period1, AnnotatedTsdlPeriod period2, int index) {
    return QueryResult.of(index, period1.period().start(), period2.period().end());
  }
}
