package org.tsdl.implementation.evaluation.impl.choice.relation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    log.debug("Evaluating 'follows' temporal operator.");
    Conditions.checkNotNull(Condition.ARGUMENT, periods, "Annotated periods as input to 'follows' operator must not be null.");
    Conditions.checkNotNull(Condition.STATE, operand1, "First event argument of 'follows' operator must not be null.");
    Conditions.checkNotNull(Condition.STATE, operand2, "Second event argument of 'follows' operator must not be null.");

    if (periods.size() < 2) {
      log.debug("Less than two annotated periods as input, hence resulting set of 'follows' periods is empty.");
      return TsdlPeriodSet.EMPTY;
    }

    var chosenPeriods = new ArrayList<TsdlPeriod>();
    for (var i = 1; i < periods.size(); i++) {
      var previousPeriod = periods.get(i - 1);
      var currentPeriod = periods.get(i);

      if (follows(previousPeriod, currentPeriod)) {
        var newPeriod = mergePeriods(previousPeriod, currentPeriod, chosenPeriods.size());
        chosenPeriods.add(newPeriod);
      }
    }

    log.debug("Evaluation of 'follows' resulted in a period set with {} periods.", chosenPeriods.size());
    return QueryResult.of(chosenPeriods.size(), chosenPeriods);
  }

  private boolean follows(AnnotatedTsdlPeriod previousPeriod, AnnotatedTsdlPeriod currentPeriod) {
    return Objects.equals(operand2().identifier(), previousPeriod.event())
        && Objects.equals(operand1().identifier(), currentPeriod.event());
  }

  private TsdlPeriod mergePeriods(AnnotatedTsdlPeriod period1, AnnotatedTsdlPeriod period2, int index) {
    return QueryResult.of(index, period1.period().start(), period2.period().end());
  }
}
