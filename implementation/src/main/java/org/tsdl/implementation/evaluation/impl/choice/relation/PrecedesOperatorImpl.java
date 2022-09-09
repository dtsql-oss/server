package org.tsdl.implementation.evaluation.impl.choice.relation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.tsdl.implementation.model.choice.AnnotatedTsdlPeriod;
import org.tsdl.implementation.model.choice.relation.PrecedesOperator;
import org.tsdl.implementation.model.choice.relation.TemporalOperand;
import org.tsdl.implementation.model.common.TsdlDuration;
import org.tsdl.implementation.model.event.TsdlEvent;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.common.TsdlUtil;
import org.tsdl.infrastructure.model.QueryResult;
import org.tsdl.infrastructure.model.TsdlPeriod;
import org.tsdl.infrastructure.model.TsdlPeriodSet;

// TODO as soon as other temporal relations are implemented, extract toleranceValue check so that the logic can be reused across operators

/**
 * Default implementation of {@link PrecedesOperator}.
 */
@Slf4j
public record PrecedesOperatorImpl(TemporalOperand operand1, TemporalOperand operand2, TsdlDuration toleranceValue) implements PrecedesOperator {
  public PrecedesOperatorImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, operand1, "First operand of 'precedes' operator must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, operand2, "Second operand of 'precedes' operator must not be null.");
  }

  @Override
  public TsdlPeriodSet evaluate(List<AnnotatedTsdlPeriod> periods) {
    // TODO distinguish between recursive operand or event operand
    var op1 = (TsdlEvent) operand1;
    var op2 = (TsdlEvent) operand2;
    log.debug("Evaluating '{} precedes {}' temporal operator.", op1.identifier().name(), op2.identifier().name());
    Conditions.checkNotNull(Condition.ARGUMENT, periods, "Annotated periods as input to 'precedes' operator must not be null.");
    Conditions.checkNotNull(Condition.STATE, op1, "First event argument of 'precedes' operator must not be null.");
    Conditions.checkNotNull(Condition.STATE, op2, "Second event argument of 'precedes' operator must not be null.");

    if (periods.size() < 2) {
      log.debug("Less than two detected periods as input, hence resulting set of 'precedes' periods must be empty.");
      return TsdlPeriodSet.EMPTY;
    }

    var chosenPeriods = new ArrayList<TsdlPeriod>();
    for (var i = 1; i < periods.size(); i++) {
      var previousPeriod = periods.get(i - 1);
      var currentPeriod = periods.get(i);

      // ensures "previous (period) precedes current (period) [WITHIN ...]"...
      var precedes = previousPeriod.subsequentDataPoint().isPresent() && satisfiesDurationConstraint(previousPeriod, currentPeriod);

      // ...we must also ensure that the previous and current periods are defined by the events represented by the operands
      //    (i.e., "operand1 = previous", "operand2 = current")...
      var operatorConformPrecedes = previousPeriod.event().equals(op1.identifier())
          && currentPeriod.event().equals(op2.identifier());

      log.debug("Events defining periods do{} conform to 'precedes' operator arguments.", operatorConformPrecedes ? "" : " not");
      if (precedes && operatorConformPrecedes) {
        log.debug("Merging period boundaries '{}' and '{}' to a chosen period.", previousPeriod.period().start(), currentPeriod.period().end());
        var mergedPeriod = QueryResult.of(chosenPeriods.size(), previousPeriod.period().start(), currentPeriod.period().end());
        chosenPeriods.add(mergedPeriod);
      }
    }

    log.debug("Evaluation of '{} precedes {}' resulted in a period set with {} periods.", op1.identifier().name(),
        op2.identifier().name(), chosenPeriods.size());
    return QueryResult.of(chosenPeriods.size(), chosenPeriods);
  }

  private boolean satisfiesDurationConstraint(AnnotatedTsdlPeriod previousPeriod, AnnotatedTsdlPeriod currentPeriod) {
    Conditions.checkIsTrue(Condition.STATE, previousPeriod.subsequentDataPoint().isPresent(),
        "Previous period's subsequent data point must be present.");
    if (toleranceValue == null) {
      return previousPeriod.subsequentDataPoint().get().timestamp().equals(currentPeriod.period().start());
    }

    var unitAdjustedTimeGap = TsdlUtil.getTimespan(
        previousPeriod.period().end(),
        currentPeriod.period().start(),
        toleranceValue.unit().modelEquivalent()
    );

    log.debug("Verifying duration constraint '{}' against time gap of {} {}.", toleranceValue, unitAdjustedTimeGap, toleranceValue.unit());
    var durationSatisfied = toleranceValue.isSatisfiedBy(unitAdjustedTimeGap);
    log.debug("Duration constraint is{} satisfied", durationSatisfied ? "" : " not");

    return durationSatisfied;
  }

  @Override
  public Optional<TsdlDuration> tolerance() {
    return Optional.ofNullable(toleranceValue);
  }
}
