package org.tsdl.client.impl.builder;

import java.util.Optional;
import org.tsdl.client.api.builder.Range;
import org.tsdl.client.api.builder.SelectOperand;
import org.tsdl.client.api.builder.SelectSpecification;

/**
 * Default implementation of {@link SelectSpecification}.
 */
public final class SelectSpecificationImpl implements SelectSpecification {
  private final SelectOperand operand1;
  private final SelectOperand operand2;
  private final Range tolerance;
  private final SelectOperator type;

  private SelectSpecificationImpl(SelectOperand operand1, SelectOperand operand2, Range tolerance, SelectOperator type) {
    this.operand1 = operand1;
    this.operand2 = operand2;
    this.tolerance = tolerance;
    this.type = type;
  }

  @Override
  public SelectOperand operand1() {
    return operand1;
  }

  @Override
  public SelectOperand operand2() {
    return operand2;
  }

  @Override
  public Optional<Range> tolerance() {
    return Optional.ofNullable(tolerance);
  }

  @Override
  public SelectOperator type() {
    return type;
  }

  public static SelectSpecification precedes(SelectOperand operand1, SelectOperand operand2, Range tolerance) {
    return new SelectSpecificationImpl(operand1, operand2, tolerance, SelectOperator.PRECEDES);
  }

  public static SelectSpecification precedes(String operand1, SelectOperand operand2, Range tolerance) {
    return precedes(EventSelectOperandImpl.eventOperand(operand1), operand2, tolerance);
  }

  public static SelectSpecification precedes(SelectOperand operand1, String operand2, Range tolerance) {
    return precedes(operand1, EventSelectOperandImpl.eventOperand(operand2), tolerance);
  }

  public static SelectSpecification precedes(String operand1, String operand2, Range tolerance) {
    return precedes(EventSelectOperandImpl.eventOperand(operand1), operand2, tolerance);
  }

  public static SelectSpecification precedes(SelectOperand operand1, SelectOperand operand2) {
    return precedes(operand1, operand2, null);
  }

  public static SelectSpecification precedes(SelectOperand operand1, String operand2) {
    return precedes(operand1, EventSelectOperandImpl.eventOperand(operand2), null);
  }

  public static SelectSpecification precedes(String operand1, SelectOperand operand2) {
    return precedes(EventSelectOperandImpl.eventOperand(operand1), operand2, null);
  }

  public static SelectSpecification precedes(String operand1, String operand2) {
    return precedes(EventSelectOperandImpl.eventOperand(operand1), EventSelectOperandImpl.eventOperand(operand2), null);
  }

  public static SelectSpecification follows(SelectOperand operand1, SelectOperand operand2, Range tolerance) {
    return new SelectSpecificationImpl(operand1, operand2, tolerance, SelectOperator.FOLLOWS);
  }

  public static SelectSpecification follows(String operand1, SelectOperand operand2, Range tolerance) {
    return follows(EventSelectOperandImpl.eventOperand(operand1), operand2, tolerance);
  }

  public static SelectSpecification follows(SelectOperand operand1, String operand2, Range tolerance) {
    return follows(operand1, EventSelectOperandImpl.eventOperand(operand2), tolerance);
  }

  public static SelectSpecification follows(String operand1, String operand2, Range tolerance) {
    return follows(EventSelectOperandImpl.eventOperand(operand1), EventSelectOperandImpl.eventOperand(operand2), tolerance);
  }

  public static SelectSpecification follows(SelectOperand operand1, SelectOperand operand2) {
    return follows(operand1, operand2, null);
  }

  public static SelectSpecification follows(String operand1, SelectOperand operand2) {
    return follows(EventSelectOperandImpl.eventOperand(operand1), operand2, null);
  }

  public static SelectSpecification follows(SelectOperand operand1, String operand2) {
    return follows(operand1, EventSelectOperandImpl.eventOperand(operand2), null);
  }

  public static SelectSpecification follows(String operand1, String operand2) {
    return follows(EventSelectOperandImpl.eventOperand(operand1), EventSelectOperandImpl.eventOperand(operand2), null);
  }
}
