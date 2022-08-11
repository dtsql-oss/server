package org.tsdl.client.impl.builder;

import java.util.Optional;
import org.tsdl.client.api.builder.ChoiceSpecification;
import org.tsdl.client.api.builder.Range;

/**
 * Default implementation of {@link ChoiceSpecification}.
 */
public final class ChoiceSpecificationImpl implements ChoiceSpecification {
  private final String operand1;
  private final String operand2;
  private final Range tolerance;
  private final ChoiceOperator type;

  private ChoiceSpecificationImpl(String operand1, String operand2, Range tolerance, ChoiceOperator type) {
    this.operand1 = operand1;
    this.operand2 = operand2;
    this.tolerance = tolerance;
    this.type = type;
  }

  @Override
  public String operand1() {
    return operand1;
  }

  @Override
  public String operand2() {
    return operand2;
  }

  @Override
  public Optional<Range> tolerance() {
    return Optional.ofNullable(tolerance);
  }

  @Override
  public ChoiceOperator type() {
    return type;
  }

  public static ChoiceSpecification precedes(String operand1, String operand2, Range tolerance) {
    return new ChoiceSpecificationImpl(operand1, operand2, tolerance, ChoiceOperator.PRECEDES);
  }

  public static ChoiceSpecification precedes(String operand1, String operand2) {
    return new ChoiceSpecificationImpl(operand1, operand2, null, ChoiceOperator.PRECEDES);
  }

  public static ChoiceSpecification follows(String operand1, String operand2, Range tolerance) {
    return new ChoiceSpecificationImpl(operand1, operand2, tolerance, ChoiceOperator.FOLLOWS);
  }

  public static ChoiceSpecification follows(String operand1, String operand2) {
    return new ChoiceSpecificationImpl(operand1, operand2, null, ChoiceOperator.FOLLOWS);
  }
}
