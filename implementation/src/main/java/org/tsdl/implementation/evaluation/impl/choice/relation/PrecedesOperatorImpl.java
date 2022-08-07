package org.tsdl.implementation.evaluation.impl.choice.relation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.tsdl.implementation.model.choice.AnnotatedTsdlPeriod;
import org.tsdl.implementation.model.choice.relation.PrecedesOperator;
import org.tsdl.implementation.model.common.TsdlDuration;
import org.tsdl.implementation.model.event.TsdlEvent;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.infrastructure.model.TsdlPeriod;
import org.tsdl.infrastructure.model.TsdlPeriodSet;

// TODO make possible to reduce inverse operators to their counterpart, e.g. precedes is the inverse of follows -
//    shouldn't have to be implemented twice (almost) exactly the same way.

/**
 * Default implementation of {@link PrecedesOperator}.
 */
@Slf4j
public record PrecedesOperatorImpl(TsdlEvent operand1, TsdlEvent operand2, TsdlDuration toleranceValue) implements PrecedesOperator {
  public PrecedesOperatorImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, operand1, "First operand of 'precedes' operator must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, operand2, "Second operand of 'precedes' operator must not be null.");
  }

  @Override
  public TsdlPeriodSet evaluate(List<AnnotatedTsdlPeriod> periods) {
    log.debug("Evaluating '{} precedes {}' temporal operator.", operand1.definition().identifier().name(), operand2.definition().identifier().name());
    Conditions.checkNotNull(Condition.ARGUMENT, periods, "Annotated periods as input to 'precedes' operator must not be null.");
    Conditions.checkNotNull(Condition.STATE, operand1, "First event argument of 'precedes' operator must not be null.");
    Conditions.checkNotNull(Condition.STATE, operand2, "Second event argument of 'precedes' operator must not be null.");

    if (periods.size() < 2) {
      log.debug("Less than two detected periods as input, hence resulting set of 'precedes' periods must be empty.");
      return TsdlPeriodSet.EMPTY;
    }

    var chosenPeriods = new ArrayList<TsdlPeriod>();
    for (var i = 1; i < periods.size(); i++) {
      var previousPeriod = periods.get(i - 1);
      var currentPeriod = periods.get(i);

      // ensures "previous precedes current"
      var precedes = previousPeriod.subsequentDataPoint().isPresent()
          && previousPeriod.subsequentDataPoint().get().timestamp().equals(currentPeriod.period().start());

      // but "previous precedes current" is not enough, we need "operand1 precedes operand" (i.e., "operand1 = previous", "operand2 = current")
      var operatorConformPrecedes = previousPeriod.event().equals(operand1.definition().identifier())
          && currentPeriod.event().equals(operand2.definition().identifier());

      if (precedes && operatorConformPrecedes) {
        var mergedPeriod = QueryResult.of(chosenPeriods.size(), previousPeriod.period().start(), currentPeriod.period().end());
        chosenPeriods.add(mergedPeriod);
      }
    }

    log.debug("Evaluation of '{} precedes {}' resulted in a period set with {} periods.", operand1.definition().identifier().name(),
        operand2.definition().identifier().name(), chosenPeriods.size());
    return QueryResult.of(chosenPeriods.size(), chosenPeriods);
  }

  @Override
  public Optional<TsdlDuration> tolerance() {
    return Optional.ofNullable(toleranceValue);
  }
}
