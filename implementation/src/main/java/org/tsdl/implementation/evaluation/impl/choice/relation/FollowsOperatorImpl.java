package org.tsdl.implementation.evaluation.impl.choice.relation;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.tsdl.implementation.model.choice.AnnotatedTsdlPeriod;
import org.tsdl.implementation.model.choice.relation.FollowsOperator;
import org.tsdl.implementation.model.event.TsdlEvent;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.infrastructure.model.TsdlPeriod;
import org.tsdl.infrastructure.model.TsdlPeriodSet;

/**
 * Default implementation of {@link FollowsOperator}.
 */
@Slf4j
public record FollowsOperatorImpl(TsdlEvent operand1, TsdlEvent operand2) implements FollowsOperator {
  public FollowsOperatorImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, operand1, "First operand of 'follows' operator must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, operand2, "Second operand of 'follows' operator must not be null.");
  }

  @Override
  public TsdlPeriodSet evaluate(List<AnnotatedTsdlPeriod> periods) {
    log.debug("Evaluating '{} follows {}' temporal operator.", operand1.definition().identifier().name(), operand2.definition().identifier().name());
    Conditions.checkNotNull(Condition.ARGUMENT, periods, "Annotated periods as input to 'follows' operator must not be null.");
    Conditions.checkNotNull(Condition.STATE, operand1, "First event argument of 'follows' operator must not be null.");
    Conditions.checkNotNull(Condition.STATE, operand2, "Second event argument of 'follows' operator must not be null.");

    if (periods.size() < 2) {
      log.debug("Less than two detected periods as input, hence resulting set of 'follows' periods must be empty.");
      return TsdlPeriodSet.EMPTY;
    }

    var chosenPeriods = new ArrayList<TsdlPeriod>();
    for (var i = 1; i < periods.size(); i++) {
      var previousPeriod = periods.get(i - 1);
      var currentPeriod = periods.get(i);

      // ensures "current follows previous"
      var follows = previousPeriod.subsequentDataPoint().isPresent()
          && previousPeriod.subsequentDataPoint().get().timestamp().equals(currentPeriod.period().start());

      // but "current follows previous" is not enough, we need "operand1 follows operand2" (i.e., "operand1 = current", "operand2 = previous")
      var operatorConformFollows = currentPeriod.event().equals(operand1.definition().identifier())
          && previousPeriod.event().equals(operand2.definition().identifier());

      if (follows && operatorConformFollows) {
        var mergedPeriod = QueryResult.of(chosenPeriods.size(), previousPeriod.period().start(), currentPeriod.period().end());
        chosenPeriods.add(mergedPeriod);
      }
    }

    log.debug("Evaluation of '{} follows {}' resulted in a period set with {} periods.", operand1.definition().identifier().name(),
        operand2.definition().identifier().name(), chosenPeriods.size());
    return QueryResult.of(chosenPeriods.size(), chosenPeriods);
  }
}
