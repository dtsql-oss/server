package org.tsdl.implementation.evaluation.impl.choice.relation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.tsdl.implementation.evaluation.impl.choice.AnnotatedTsdlPeriodImpl;
import org.tsdl.implementation.evaluation.impl.common.TsdlIdentifierImpl;
import org.tsdl.implementation.model.choice.AnnotatedTsdlPeriod;
import org.tsdl.implementation.model.choice.relation.PrecedesOperator;
import org.tsdl.implementation.model.choice.relation.TemporalOperand;
import org.tsdl.implementation.model.choice.relation.TemporalOperator;
import org.tsdl.implementation.model.common.TsdlDuration;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.common.TsdlUtil;
import org.tsdl.infrastructure.model.QueryResult;

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
  public List<AnnotatedTsdlPeriod> evaluate(List<AnnotatedTsdlPeriod> periods) {
    log.debug("Evaluating '{}' temporal operator.", representation());
    Conditions.checkNotNull(Condition.ARGUMENT, periods, "Annotated periods as input to '%s' operator must not be null.", representation());
    Conditions.checkNotNull(Condition.STATE, operand1, "First event argument of '%s' operator must not be null.", representation());
    Conditions.checkNotNull(Condition.STATE, operand2, "Second event argument of '%s' operator must not be null.", representation());

    var periodsToExamine = new ArrayList<>(periods);
    if ((operand1 instanceof TemporalOperator op1)) {
      var op1Periods = op1.evaluate(periods);
      periodsToExamine.addAll(op1Periods);
    }
    if ((operand2 instanceof TemporalOperator op2)) {
      var op2Periods = op2.evaluate(periods);
      periodsToExamine.addAll(op2Periods);
    }

    if (periodsToExamine.size() < 2) {
      log.debug("Less than two detected periods as input, hence the resulting set of '{}' periods must be empty.", representation());
      return List.of();
    }

    // TODO possible to solve without quadratic behaviour? e.g. sort periodsToExamine by start date and, like before, iterate pairwise, considering
    //  periods with same start date at the same time
    var chosenPeriods = new ArrayList<AnnotatedTsdlPeriod>();
    for (var i = 0; i < periodsToExamine.size(); i++) {
      var currentPeriod = periodsToExamine.get(i);
      for (var j = 0; j < i; j++) {
        var otherPeriod = periodsToExamine.get(j);
        // ensures "previous (period) precedes current (period) [WITHIN ...]"...
        var precedes = otherPeriod.subsequentDataPoint().isPresent() && satisfiesDurationConstraint(otherPeriod, currentPeriod);

        // ...we must also ensure that the previous and current periods are defined by the events represented by the operands
        //    (i.e., "operand1 = previous", "operand2 = current")...
        var operatorConformPrecedes = otherPeriod.event().representation().equals(operand1.representation())
            && currentPeriod.event().representation().equals(operand2.representation());

        log.debug("Events defining periods do{} conform to '{}' operator arguments.", operatorConformPrecedes ? "" : " not", representation());
        if (precedes && operatorConformPrecedes) {
          log.debug("Merging period boundaries '{}' and '{}' to a chosen period.", otherPeriod.period().start(), currentPeriod.period().end());
          var mergedPeriod = QueryResult.of(chosenPeriods.size(), otherPeriod.period().start(), currentPeriod.period().end());
          var mergedAnnotatedPeriod = new AnnotatedTsdlPeriodImpl(
              mergedPeriod,
              new TsdlIdentifierImpl("%s".formatted(representation())),
              otherPeriod.priorDataPoint().orElse(null),
              currentPeriod.subsequentDataPoint().orElse(null)
          );
          chosenPeriods.add(mergedAnnotatedPeriod);
        }
      }
    }

    log.debug("Evaluation of '{}' resulted in a period set with {} periods.", representation(), chosenPeriods.size());
    return chosenPeriods;
  }

  @Override
  public String representation() {
    return "(%s-precedes-%s)".formatted(operand1.representation(), operand2.representation());
  }

  @Override
  public Optional<TsdlDuration> tolerance() {
    return Optional.ofNullable(toleranceValue);
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
}
